package atlantis2679.lib.tunables;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.BiConsumer;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import atlantis2679.lib.tunables.sendableproperties.TunableProperty;

public class TunablesManager {
    private static Queue<TunableItem> newTunablesQueue = new LinkedList<>();
    private static boolean isEnabled = false;

    public static void add(String key, Tunable tunable, BiConsumer<String, Sendable> sendablePublisher) {
        // does not instantly publish even when enabled to avoid publishing from inside
        // initTunable addChild() call, beacause doing that leads to
        // ConcurrentModificationException with shuffleboard.
        newTunablesQueue.add(new TunableItem(key, tunable, sendablePublisher));
    }

    public static void add(String key, Tunable tunable) {
        add(key, tunable, SmartDashboard::putData);
    }

    public static void add(String key, Sendable sendable, BiConsumer<String, Sendable> sendablePublisher) {
        newTunablesQueue.add(new TunableItem(key, sendable::initSendable, sendablePublisher));
    }

    public static void add(String key, Sendable sendable) {
        add(key, sendable, SmartDashboard::putData);
    }

    public static void enable() {
        if (!isEnabled) {
            isEnabled = true;
            update();
        }
    }

    private static void publishTunable(
            String name,
            Tunable tunable,
            BiConsumer<String, Sendable> sendablePublisher) {
        sendablePublisher.accept(name, (builder) -> {
            tunable.initTunable(new TunableBuilder(builder, name, sendablePublisher));
        });
    }

    public static void update() {
        if (isEnabled) {
            TunableProperty.updateAll();

            while (!newTunablesQueue.isEmpty()) {
                TunableItem item = newTunablesQueue.poll();
                publishTunable(item.key, item.tunable, item.sendablePublisher);
            }
        }
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    private static class TunableItem {
        public final String key;
        public final Tunable tunable;
        public final BiConsumer<String, Sendable> sendablePublisher;

        public TunableItem(String key, Tunable tunable, BiConsumer<String, Sendable> sendablePublisher) {
            this.key = key;
            this.tunable = tunable;
            this.sendablePublisher = sendablePublisher;
        }
    }
}
