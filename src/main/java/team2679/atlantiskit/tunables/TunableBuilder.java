package team2679.atlantiskit.tunables;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import edu.wpi.first.networktables.NTSendableBuilder;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.Topic;
import edu.wpi.first.util.function.BooleanConsumer;
import edu.wpi.first.util.function.FloatConsumer;
import edu.wpi.first.util.function.FloatSupplier;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import team2679.atlantiskit.logfields.LogFieldsTable;
import team2679.atlantiskit.tunables.sendableproperties.BooleanTunableProperty;
import team2679.atlantiskit.tunables.sendableproperties.NumberTunableProperty;
import team2679.atlantiskit.tunables.sendableproperties.StringTunableProperty;

public class TunableBuilder implements NTSendableBuilder {
    private final SendableBuilder baseBuilder;
    private final LogFieldsTable fieldsTable;
    private final BiConsumer<String, Sendable> sendablePublisher;
    private final String key;

    public TunableBuilder(SendableBuilder baseBuilder, String key, BiConsumer<String, Sendable> sendablePublisher) {
        this.baseBuilder = baseBuilder;
        this.key = key;
        this.fieldsTable = new LogFieldsTable("Tunables/" + key);
        this.sendablePublisher = sendablePublisher;
    }

    // ===================== new methods =====================

    public void setSendableType(SendableType sendableType) {
        setSmartDashboardType(sendableType.getStringType());
    }

    public void addChild(String name, Tunable tunable) {
        TunablesManager.add(this.key + "/" + name, tunable, sendablePublisher);
    }

    public void addChild(String name, Sendable sendable) {
        TunablesManager.add(this.key + "/" + name, sendable, sendablePublisher);
    }

    // ===================== methods with no change =====================

    @Override
    public void addBooleanProperty(String key, BooleanSupplier getter, BooleanConsumer setter) {
        if (setter != null)
            new BooleanTunableProperty(key, getter, setter, fieldsTable, baseBuilder);
        else
            baseBuilder.addBooleanProperty(key, getter, setter);
    }

    @Override
    public void addIntegerProperty(String key, LongSupplier getter, LongConsumer setter) {
        baseBuilder.addIntegerProperty(key, getter, null);
    }

    @Override
    public void addFloatProperty(String key, FloatSupplier getter, FloatConsumer setter) {
        baseBuilder.addFloatProperty(key, getter, null);
    }

    @Override
    public void addDoubleProperty(String key, DoubleSupplier getter, DoubleConsumer setter) {
        if (setter != null)
            new NumberTunableProperty(key, getter, setter, fieldsTable, baseBuilder);
        else
            baseBuilder.addDoubleProperty(key, getter, setter);
    }

    @Override
    public void addStringProperty(String key, Supplier<String> getter, Consumer<String> setter) {
        if (setter != null)
            new StringTunableProperty(key, getter, setter, fieldsTable, baseBuilder);
        else
            baseBuilder.addStringProperty(key, getter, setter);
    }

    @Override
    public void addBooleanArrayProperty(String key, Supplier<boolean[]> getter, Consumer<boolean[]> setter) {
        baseBuilder.addBooleanArrayProperty(key, getter, null);
    }

    @Override
    public void addIntegerArrayProperty(String key, Supplier<long[]> getter, Consumer<long[]> setter) {
        baseBuilder.addIntegerArrayProperty(key, getter, null);
    }

    @Override
    public void addFloatArrayProperty(String key, Supplier<float[]> getter, Consumer<float[]> setter) {
        baseBuilder.addFloatArrayProperty(key, getter, null);
    }

    @Override
    public void addDoubleArrayProperty(String key, Supplier<double[]> getter, Consumer<double[]> setter) {
        baseBuilder.addDoubleArrayProperty(key, getter, null);
    }

    @Override
    public void addStringArrayProperty(String key, Supplier<String[]> getter, Consumer<String[]> setter) {
        baseBuilder.addStringArrayProperty(key, getter, null);
    }

    @Override
    public void addRawProperty(String key, String typeString, Supplier<byte[]> getter, Consumer<byte[]> setter) {
        baseBuilder.addRawProperty(key, typeString, getter, null);
    }

    // ===================== methods with no change =====================

    @Override
    public void close() throws Exception {
        baseBuilder.close();
    }

    @Override
    public void setSmartDashboardType(String type) {
        baseBuilder.setSmartDashboardType(type);
    }

    @Override
    public void setActuator(boolean value) {
        baseBuilder.setActuator(value);
    }

    @Override
    public void setSafeState(Runnable func) {
        baseBuilder.setSafeState(func);
    }

    @Override
    public BackendKind getBackendKind() {
        return baseBuilder.getBackendKind();
    }

    @Override
    public boolean isPublished() {
        return baseBuilder.isPublished();
    }

    @Override
    public void update() {
        baseBuilder.update();
    }

    @Override
    public void clearProperties() {
        baseBuilder.clearProperties();
    }

    @Override
    public void addCloseable(AutoCloseable closeable) {
        baseBuilder.addCloseable(closeable);
    }

    @Override
    public void setUpdateTable(Runnable func) {
        ((NTSendableBuilder) baseBuilder).setUpdateTable(func);
    }

    @Override
    public Topic getTopic(String key) {
        return ((NTSendableBuilder) baseBuilder).getTopic(key);
    }

    @Override
    public NetworkTable getTable() {
        return ((NTSendableBuilder) baseBuilder).getTable();
    }

    @Override
    public void publishConstBoolean(String key, boolean value) {
        baseBuilder.publishConstBoolean(key, value);
    }

    @Override
    public void publishConstInteger(String key, long value) {
        baseBuilder.publishConstInteger(key, value);
    }

    @Override
    public void publishConstFloat(String key, float value) {
        baseBuilder.publishConstFloat(key, value);
    }

    @Override
    public void publishConstDouble(String key, double value) {
        baseBuilder.publishConstDouble(key, value);
    }

    @Override
    public void publishConstString(String key, String value) {
        baseBuilder.publishConstString(key, value);
    }

    @Override
    public void publishConstBooleanArray(String key, boolean[] value) {
        baseBuilder.publishConstBooleanArray(key, value);
    }

    @Override
    public void publishConstIntegerArray(String key, long[] value) {
        baseBuilder.publishConstIntegerArray(key, value);
    }

    @Override
    public void publishConstFloatArray(String key, float[] value) {
        baseBuilder.publishConstFloatArray(key, value);
    }

    @Override
    public void publishConstDoubleArray(String key, double[] value) {
        baseBuilder.publishConstDoubleArray(key, value);
    }

    @Override
    public void publishConstStringArray(String key, String[] value) {
        baseBuilder.publishConstStringArray(key, value);
    }

    @Override
    public void publishConstRaw(String key, String typeString, byte[] value) {
        baseBuilder.publishConstRaw(key, typeString, value);
    }
}
