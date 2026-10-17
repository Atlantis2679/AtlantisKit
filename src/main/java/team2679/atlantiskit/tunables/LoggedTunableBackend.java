package team2679.atlantiskit.tunables;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.inputs.LoggableInputs;
import org.wpilib.tunable.ComplexTunable;
import org.wpilib.tunable.Tunable;
import org.wpilib.tunable.TunableBackend;
import org.wpilib.tunable.TunableBase;
import org.wpilib.tunable.TunableBoolean;
import org.wpilib.tunable.TunableConfig;
import org.wpilib.tunable.TunableDouble;
import org.wpilib.tunable.TunableFloat;
import org.wpilib.tunable.TunableInt;
import org.wpilib.tunable.TunableLong;
import org.wpilib.tunable.TunableRegistry;
import org.wpilib.tunable.util.PathUtil;
import org.wpilib.util.protobuf.Protobuf;
import org.wpilib.util.struct.Struct;

/**
 * A {@link TunableBackend} that makes WPILib Tunables replay-safe with AdvantageKit.
 *
 * <p>Every value written from the dashboard is recorded as an AdvantageKit input. In replay, the
 * dashboard is ignored and the recorded writes are applied instead, at the same point in the loop,
 * so the robot code sees exactly the same tunable values as it did on the real robot.
 *
 * <p>Display and dashboard I/O are delegated to another backend (normally WPILib's {@code
 * NetworkTablesTunableBackend}). The delegate never sees the real tunables: it gets a proxy for
 * each one, whose getter shows the real value and whose setter only records the write.
 *
 * <p>Usage, in the Robot constructor <b>after</b> {@code Logger.start()}:
 *
 * <pre>{@code
 * TunableRegistry.registerBackend("",
 *     new LoggedTunableBackend(
 *         new NetworkTablesTunableBackend(NetworkTableInstance.getDefault(), "/Tunables")));
 * }</pre>
 *
 * <p>Registering on the {@code ""} prefix replaces WPILib's default backend and migrates any
 * tunables that were already published.
 */
public final class LoggedTunableBackend implements TunableBackend {
  private final TunableBackend m_display;
  private final String m_logKey;

  // TreeMaps give a fixed iteration order, so writes and callbacks are applied in the same order in
  // real and replay. All access happens under the registry's update mutex or this object's lock.
  private final TreeMap<String, Leaf> m_leaves = new TreeMap<>();
  private final TreeMap<String, ComplexTunable> m_complexes = new TreeMap<>();
  private final Inputs m_inputs = new Inputs();

  /**
   * Creates a backend that logs under the "Tunables" inputs key.
   *
   * @param displayBackend backend used to show values and receive dashboard writes
   */
  public LoggedTunableBackend(TunableBackend displayBackend) {
    this(displayBackend, "Tunables");
  }

  /**
   * Creates a backend.
   *
   * @param displayBackend backend used to show values and receive dashboard writes
   * @param logKey AdvantageKit inputs key under which dashboard writes are logged
   */
  public LoggedTunableBackend(TunableBackend displayBackend, String logKey) {
    if (displayBackend == null) {
      throw new IllegalArgumentException("displayBackend must not be null");
    }
    m_display = displayBackend;
    m_logKey = logKey;
  }

  @Override
  public synchronized boolean publish(String path, TunableBase tunable) {
    path = PathUtil.normalizeName(path);
    if (tunable instanceof Tunable.CustomTunable custom) {
      tunable = custom.getInnerTunable();
    }
    if (m_leaves.containsKey(path)) {
      TunableRegistry.reportWarning("Tunable already exists: " + path);
      return false;
    }

    Leaf leaf = new Leaf(path, tunable);
    Codec codec = Codec.forTunable(tunable);
    leaf.codec = codec;
    TunableBase proxy = codec == null ? null : codec.createProxy(leaf, proxyConfig(tunable));
    if (proxy == null) {
      // Not replay-safe: let the dashboard drive it directly, but say so.
      TunableRegistry.reportWarning(
          "Tunable '"
              + path
              + "' of type "
              + tunable.getClass().getName()
              + " cannot be logged by AdvantageKit; it will work on the robot but NOT in replay");
      return m_display.publish(path, tunable);
    }
    leaf.proxy = proxy;

    // Register before publishing: the delegate may apply an existing dashboard value right away.
    m_leaves.put(path, leaf);
    if (!m_display.publish(path, proxy)) {
      m_leaves.remove(path);
      return false;
    }
    return true;
  }

  @Override
  public synchronized boolean publishComplex(String path, ComplexTunable tunable) {
    path = PathUtil.normalizeName(path);
    if (m_complexes.containsKey(path)) {
      TunableRegistry.reportWarning("Tunable already exists: " + path);
      return false;
    }
    m_complexes.put(path, tunable);
    // The delegate publishes the complex object's type info and calls
    // TunableRegistry.publishComplexChildren(), which routes each child back to publish() here.
    if (!m_display.publishComplex(path, tunable)) {
      m_complexes.remove(path);
      return false;
    }
    return true;
  }

  @Override
  public synchronized void remove(String path) {
    path = PathUtil.normalizeName(path);
    m_leaves.remove(path);
    if (m_complexes.remove(path) != null) {
      String childPrefix = path + "/";
      m_leaves.keySet().removeIf(p -> p.startsWith(childPrefix));
    }
    m_display.remove(path);
  }

  @Override
  public synchronized List<PublishedTunable> removePrefix(String prefix) {
    String normalized = PathUtil.normalizePrefix(prefix);
    List<PublishedTunable> removed = new ArrayList<>();
    IdentityHashMap<TunableBase, Boolean> proxies = new IdentityHashMap<>();
    for (Leaf leaf : m_leaves.values()) {
      proxies.put(leaf.proxy, Boolean.TRUE);
    }
    for (Iterator<Map.Entry<String, ComplexTunable>> it = m_complexes.entrySet().iterator();
        it.hasNext(); ) {
      var entry = it.next();
      if (PathUtil.isPathOrDescendant(entry.getKey(), normalized)) {
        removed.add(new PublishedTunable(entry.getKey(), null, entry.getValue()));
        it.remove();
      }
    }
    for (Iterator<Map.Entry<String, Leaf>> it = m_leaves.entrySet().iterator(); it.hasNext(); ) {
      var entry = it.next();
      if (PathUtil.isPathOrDescendant(entry.getKey(), normalized)) {
        // Hand back the REAL tunable so it migrates correctly, never the proxy.
        removed.add(new PublishedTunable(entry.getKey(), entry.getValue().real, null));
        it.remove();
      }
    }
    // Unlogged fallbacks were published to the delegate directly; return those too.
    for (PublishedTunable p : m_display.removePrefix(prefix)) {
      if (p.complex() == null && p.tunable() != null && !proxies.containsKey(p.tunable())) {
        removed.add(p);
      }
    }
    return removed;
  }

  @Override
  public synchronized void update() {
    boolean replay = Logger.hasReplaySource();

    // 1. Let the delegate refresh the dashboard. On the real robot this is also where dashboard
    //    writes arrive (captured by the proxies). In replay the proxies discard them. Called in both
    //    modes so ComplexTunable.updateTunable() runs identically.
    m_inputs.m_acceptWrites = !replay;
    m_display.update();

    // 2. Turn this cycle's captured writes into numbered revisions.
    if (!replay) {
      for (Leaf leaf : m_leaves.values()) {
        if (leaf.pending != null) {
          leaf.revision++;
          leaf.value = leaf.pending;
          leaf.pending = null;
        }
      }
    }

    // 3. Record (real) or restore (replay) the writes.
    Logger.processInputs(m_logKey, m_inputs);

    // 4. Apply every write that hasn't been applied yet, in path order.
    for (Leaf leaf : m_leaves.values()) {
      if (leaf.revision != leaf.appliedRevision) {
        leaf.appliedRevision = leaf.revision;
        if (leaf.value != null) {
          leaf.codec.apply(leaf.real, leaf.value);
          Runnable onTune = leaf.real.getConfig() == null ? null : leaf.real.getConfig().getOnTune();
          if (onTune != null) {
            TunableRegistry.runAfterUpdate(onTune);
          }
        }
      }
      if (leaf.real.hasChanged()) {
        TunableRegistry.resetChangedAfterUpdate(leaf.real);
      }
    }
  }

  @Override
  public void markDirty(TunableBase tunable) {
    // Proxies always poll the real value, so there's nothing to forward.
  }

  @Override
  public void close() throws Exception {
    m_display.close();
  }

  private static TunableConfig proxyConfig(TunableBase real) {
    TunableConfig config = real.getConfig() == null ? new TunableConfig() : real.getConfig();
    // onTune belongs to the real tunable and is run by this backend after the write is applied.
    // The proxy can't be notified of local changes, so it must poll.
    return config.withOnTune(null).withPolling(TunableConfig.Polling.ALWAYS_GET);
  }

  /** The AdvantageKit inputs: one revision counter and latest written value per tuned leaf. */
  private final class Inputs implements LoggableInputs {
    private boolean m_acceptWrites = true;

    @Override
    public void toLog(LogTable table) {
      for (Leaf leaf : m_leaves.values()) {
        if (leaf.revision > 0) {
          table.put(leaf.key + "/Revision", leaf.revision);
          leaf.codec.put(table, leaf.key + "/Value", leaf.value);
        }
      }
    }

    @Override
    public void fromLog(LogTable table) {
      for (Leaf leaf : m_leaves.values()) {
        long revision = table.get(leaf.key + "/Revision", 0L);
        if (revision != leaf.revision) {
          leaf.revision = revision;
          leaf.value = leaf.codec.get(table, leaf.key + "/Value");
        }
      }
    }
  }

  /** State for one published tunable value. */
  private final class Leaf {
    @SuppressWarnings("unused")
    final String path;
    final String key; // log key, without the leading '/'
    final TunableBase real;
    Codec codec;
    TunableBase proxy;

    Object pending; // dashboard write captured this cycle (real robot only)
    long revision; // number of dashboard writes so far
    Object value; // value of the latest write
    long appliedRevision;

    Leaf(String path, TunableBase real) {
      this.path = path;
      this.key = path.startsWith("/") ? path.substring(1) : path;
      this.real = real;
    }

    /** What the dashboard should show: a write in flight, otherwise the real value. */
    Object display() {
      return pending != null ? pending : codec.read(real);
    }

    void capture(Object value) {
      if (m_inputs.m_acceptWrites && value != null) {
        pending = value;
      }
    }
  }

  /** Per-type glue: proxy creation, reading/writing the real tunable, and log serialization. */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private abstract static class Codec {
    abstract TunableBase createProxy(Leaf leaf, TunableConfig config);

    abstract Object read(TunableBase real);

    abstract void apply(TunableBase real, Object value);

    abstract void put(LogTable table, String key, Object value);

    abstract Object get(LogTable table, String key);

    static Codec forTunable(TunableBase t) {
      if (t instanceof TunableBoolean) {
        return BOOLEAN;
      } else if (t instanceof TunableInt) {
        return INT;
      } else if (t instanceof TunableLong) {
        return LONG;
      } else if (t instanceof TunableFloat) {
        return FLOAT;
      } else if (t instanceof TunableDouble) {
        return DOUBLE;
      } else if (t instanceof Tunable.TunableStruct<?> s) {
        return new StructCodec(s.getStruct(), s.getTypeClass());
      } else if (t instanceof Tunable.TunableStructArray<?> s) {
        return new StructArrayCodec(s.getStruct(), s.getTypeClass());
      } else if (t instanceof Tunable.TunableProtobuf<?> p) {
        return new ProtobufCodec(p.getProtobuf(), p.getTypeClass());
      } else if (t instanceof Tunable<?> g) {
        return BasicCodec.forClass(g.getTypeClass());
      }
      return null;
    }

    // Typed helpers so Java picks LogTable's single-struct vs struct-array overloads correctly.
    static <T> void putStruct(LogTable t, String k, Struct<T> s, Object v) {
      t.put(k, s, (T) v);
    }

    static <T> Object getStruct(LogTable t, String k, Struct<T> s) {
      T def = null;
      return t.get(k, s, def);
    }

    static <T> void putStructArray(LogTable t, String k, Struct<T> s, Object[] v) {
      T[] arr = (T[]) v;
      t.put(k, s, arr);
    }

    static <T> Object getStructArray(LogTable t, String k, Struct<T> s) {
      T[] empty = (T[]) Array.newInstance(s.getTypeClass(), 0);
      return t.get(k, s, empty);
    }

    static <T> void putProto(LogTable t, String k, Protobuf<T, ?> p, Object v) {
      t.put(k, p, (T) v);
    }

    static <T> Object getProto(LogTable t, String k, Protobuf<T, ?> p) {
      T def = null;
      return t.get(k, p, def);
    }

    /** Builds a getter/setter proxy for a generic Tunable and checks it kept the serializer. */
    static TunableBase genericProxy(Leaf leaf, Class cls, TunableConfig config, Class expected) {
      Tunable<Object> proxy =
          Tunable.createConfig((Supplier) leaf::display, (Consumer) leaf::capture, cls, config);
      return expected.isInstance(proxy) ? proxy : null;
    }

    static final Codec BOOLEAN =
        new Codec() {
          TunableBase createProxy(Leaf leaf, TunableConfig config) {
            return TunableBoolean.createConfig(
                () -> (Boolean) leaf.display(), v -> leaf.capture(v), config);
          }

          Object read(TunableBase real) {
            return ((TunableBoolean) real).get();
          }

          void apply(TunableBase real, Object v) {
            ((TunableBoolean) real).set((Boolean) v);
          }

          void put(LogTable t, String k, Object v) {
            t.put(k, (boolean) (Boolean) v);
          }

          Object get(LogTable t, String k) {
            return t.get(k, false);
          }
        };

    static final Codec INT =
        new Codec() {
          TunableBase createProxy(Leaf leaf, TunableConfig config) {
            return TunableInt.createConfig(
                () -> (Integer) leaf.display(), v -> leaf.capture(v), config);
          }

          Object read(TunableBase real) {
            return ((TunableInt) real).get();
          }

          void apply(TunableBase real, Object v) {
            ((TunableInt) real).set((Integer) v);
          }

          void put(LogTable t, String k, Object v) {
            t.put(k, (int) (Integer) v);
          }

          Object get(LogTable t, String k) {
            return t.get(k, 0);
          }
        };

    static final Codec LONG =
        new Codec() {
          TunableBase createProxy(Leaf leaf, TunableConfig config) {
            return TunableLong.createConfig(
                () -> (Long) leaf.display(), v -> leaf.capture(v), config);
          }

          Object read(TunableBase real) {
            return ((TunableLong) real).get();
          }

          void apply(TunableBase real, Object v) {
            ((TunableLong) real).set((Long) v);
          }

          void put(LogTable t, String k, Object v) {
            t.put(k, (long) (Long) v);
          }

          Object get(LogTable t, String k) {
            return t.get(k, 0L);
          }
        };

    static final Codec FLOAT =
        new Codec() {
          TunableBase createProxy(Leaf leaf, TunableConfig config) {
            return TunableFloat.createConfig(
                () -> (Float) leaf.display(), v -> leaf.capture(v), config);
          }

          Object read(TunableBase real) {
            return ((TunableFloat) real).get();
          }

          void apply(TunableBase real, Object v) {
            ((TunableFloat) real).set((Float) v);
          }

          void put(LogTable t, String k, Object v) {
            t.put(k, (float) (Float) v);
          }

          Object get(LogTable t, String k) {
            return t.get(k, 0.0f);
          }
        };

    static final Codec DOUBLE =
        new Codec() {
          TunableBase createProxy(Leaf leaf, TunableConfig config) {
            return TunableDouble.createConfig(
                () -> (Double) leaf.display(), v -> leaf.capture(v), config);
          }

          Object read(TunableBase real) {
            return ((TunableDouble) real).get();
          }

          void apply(TunableBase real, Object v) {
            ((TunableDouble) real).set((Double) v);
          }

          void put(LogTable t, String k, Object v) {
            t.put(k, (double) (Double) v);
          }

          Object get(LogTable t, String k) {
            return t.get(k, 0.0);
          }
        };

    /** Generic Tunable<T> of a plain type (boxed primitives, String, arrays). */
    abstract static class BasicCodec extends Codec {
      final Class m_cls;

      BasicCodec(Class cls) {
        m_cls = cls;
      }

      TunableBase createProxy(Leaf leaf, TunableConfig config) {
        return genericProxy(leaf, m_cls, config, Tunable.class);
      }

      Object read(TunableBase real) {
        return ((Tunable) real).get();
      }

      void apply(TunableBase real, Object v) {
        ((Tunable) real).set(v);
      }

      static Codec forClass(Class<?> cls) {
        if (cls == Boolean.class) {
          return basic(cls, (t, k, v) -> t.put(k, (boolean) (Boolean) v), (t, k) -> t.get(k, false));
        } else if (cls == Integer.class) {
          return basic(cls, (t, k, v) -> t.put(k, (int) (Integer) v), (t, k) -> t.get(k, 0));
        } else if (cls == Long.class) {
          return basic(cls, (t, k, v) -> t.put(k, (long) (Long) v), (t, k) -> t.get(k, 0L));
        } else if (cls == Float.class) {
          return basic(cls, (t, k, v) -> t.put(k, (float) (Float) v), (t, k) -> t.get(k, 0.0f));
        } else if (cls == Double.class) {
          return basic(cls, (t, k, v) -> t.put(k, (double) (Double) v), (t, k) -> t.get(k, 0.0));
        } else if (cls == String.class) {
          return basic(cls, (t, k, v) -> t.put(k, (String) v), (t, k) -> t.get(k, ""));
        } else if (cls == byte[].class) {
          return basic(cls, (t, k, v) -> t.put(k, (byte[]) v), (t, k) -> t.get(k, new byte[0]));
        } else if (cls == boolean[].class) {
          return basic(
              cls, (t, k, v) -> t.put(k, (boolean[]) v), (t, k) -> t.get(k, new boolean[0]));
        } else if (cls == int[].class) {
          return basic(cls, (t, k, v) -> t.put(k, (int[]) v), (t, k) -> t.get(k, new int[0]));
        } else if (cls == long[].class) {
          return basic(cls, (t, k, v) -> t.put(k, (long[]) v), (t, k) -> t.get(k, new long[0]));
        } else if (cls == float[].class) {
          return basic(cls, (t, k, v) -> t.put(k, (float[]) v), (t, k) -> t.get(k, new float[0]));
        } else if (cls == double[].class) {
          return basic(
              cls, (t, k, v) -> t.put(k, (double[]) v), (t, k) -> t.get(k, new double[0]));
        } else if (cls == String[].class) {
          return basic(
              cls, (t, k, v) -> t.put(k, (String[]) v), (t, k) -> t.get(k, new String[0]));
        }
        return null;
      }

      interface Putter {
        void put(LogTable t, String k, Object v);
      }

      interface Getter {
        Object get(LogTable t, String k);
      }

      static Codec basic(Class<?> cls, Putter putter, Getter getter) {
        return new BasicCodec(cls) {
          void put(LogTable t, String k, Object v) {
            putter.put(t, k, v);
          }

          Object get(LogTable t, String k) {
            return getter.get(t, k);
          }
        };
      }
    }

    static final class StructCodec extends Codec {
      final Struct m_struct;
      final Class m_cls;

      StructCodec(Struct<?> struct, Class<?> cls) {
        m_struct = struct;
        m_cls = cls;
      }

      TunableBase createProxy(Leaf leaf, TunableConfig config) {
        // Works for StructSerializable types (the normal case). A struct given explicitly for a
        // non-StructSerializable class can't be proxied, and falls back to unlogged.
        return genericProxy(leaf, m_cls, config, Tunable.TunableStruct.class);
      }

      Object read(TunableBase real) {
        return ((Tunable) real).get();
      }

      void apply(TunableBase real, Object v) {
        ((Tunable) real).set(v);
      }

      void put(LogTable t, String k, Object v) {
        putStruct(t, k, m_struct, v);
      }

      Object get(LogTable t, String k) {
        return getStruct(t, k, m_struct);
      }
    }

    static final class StructArrayCodec extends Codec {
      final Struct m_struct;
      final Class m_cls;

      StructArrayCodec(Struct<?> struct, Class<?> cls) {
        m_struct = struct;
        m_cls = cls;
      }

      TunableBase createProxy(Leaf leaf, TunableConfig config) {
        return genericProxy(leaf, m_cls, config, Tunable.TunableStructArray.class);
      }

      Object read(TunableBase real) {
        return ((Tunable) real).get();
      }

      void apply(TunableBase real, Object v) {
        ((Tunable) real).set(v);
      }

      void put(LogTable t, String k, Object v) {
        putStructArray(t, k, m_struct, (Object[]) v);
      }

      Object get(LogTable t, String k) {
        return getStructArray(t, k, m_struct);
      }
    }

    static final class ProtobufCodec extends Codec {
      final Protobuf m_proto;
      final Class m_cls;

      ProtobufCodec(Protobuf<?, ?> proto, Class<?> cls) {
        m_proto = proto;
        m_cls = cls;
      }

      TunableBase createProxy(Leaf leaf, TunableConfig config) {
        return genericProxy(leaf, m_cls, config, Tunable.TunableProtobuf.class);
      }

      Object read(TunableBase real) {
        return ((Tunable) real).get();
      }

      void apply(TunableBase real, Object v) {
        ((Tunable) real).set(v);
      }

      void put(LogTable t, String k, Object v) {
        putProto(t, k, m_proto, v);
      }

      Object get(LogTable t, String k) {
        return getProto(t, k, m_proto);
      }
    }
  }
}