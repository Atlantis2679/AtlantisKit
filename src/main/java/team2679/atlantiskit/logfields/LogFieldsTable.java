package team2679.atlantiskit.logfields;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.inputs.LoggableInputs;
import org.littletonrobotics.junction.mechanism.LoggedMechanism2d;

import team2679.atlantiskit.logfields.logfields.BooleanLogField;
import team2679.atlantiskit.logfields.logfields.DoubleLogField;
import team2679.atlantiskit.logfields.logfields.IntegerLogField;
import team2679.atlantiskit.logfields.logfields.LogField;

import org.wpilib.units.Measure;
import org.wpilib.units.Unit;
import org.wpilib.util.WPISerializable;
import org.wpilib.util.struct.StructSerializable;

public class LogFieldsTable implements LoggableInputs {
    private final static ArrayList<LogFieldsTable> createdTables = new ArrayList<>();

    private final String name;
    private final String prefix;
    private final List<LoggableInputs> fields = new ArrayList<>();
    private Runnable periodicBeforeFields = null;

    public LogFieldsTable(String name) {
        this.name = name;
        prefix = name + "/";
    }

    public static void updateAllTables() {
        for (LogFieldsTable fieldsTable : createdTables) {
            fieldsTable.update();
        }
    }

    public void update() {
        if (periodicBeforeFields != null && !Logger.hasReplaySource()) {
            periodicBeforeFields.run();
        }
        Logger.processInputs(name, this);
    }

    public LogFieldsTable getSubTable(String name) {
        return new LogFieldsTable(prefix + name);
    }

    @Override
    public void toLog(LogTable table) {
        for (LoggableInputs field : fields) {
            field.toLog(table);
        }
    }

    @Override
    public void fromLog(LogTable table) {
        for (LoggableInputs field : fields) {
            field.fromLog(table);
        }
    }

    public void setPeriodicBeforeFields(Runnable periodicRunnable) {
        this.periodicBeforeFields = periodicRunnable;
    }

    private <T extends LoggableInputs> T registerField(T field) {
        if (fields.isEmpty()) {
            // only add the table when it has fields, to not save each subTable created in
            // periodic for outputs recording (because that leads to saving tons of unneeded
            // fields table that doesn't have any fields).
            createdTables.add(this);
        }
        fields.add(field);
        return field;
    }

    public BooleanSupplier addBoolean(
            String name,
            BooleanSupplier valueSupplier,
            boolean defaultValue) {
        return registerField(new BooleanLogField(name, valueSupplier, defaultValue));
    }

    public BooleanSupplier addBoolean(String name, BooleanSupplier valueSupplier) {
        return addBoolean(name, valueSupplier, false);
    }

    public IntSupplier addInteger(
            String name,
            IntSupplier valueSupplier,
            int defaultValue) {
        return registerField(new IntegerLogField(name, valueSupplier, defaultValue));
    }

    public IntSupplier addInteger(String name, IntSupplier valueSupplier) {
        return addInteger(name, valueSupplier, 0);
    }

    public DoubleSupplier addDouble(
            String name,
            DoubleSupplier valueSupplier,
            double defaultValue) {
        return registerField(new DoubleLogField(name, valueSupplier, defaultValue));
    }

    public DoubleSupplier addDouble(String name, DoubleSupplier valueSupplier) {
        return addDouble(name, valueSupplier, 0);
    }

    public Supplier<String> addString(
            String name,
            Supplier<String> valueSupplier,
            String defaultValue) {
        return registerField(new LogField<>(name, valueSupplier, LogTable::get, LogTable::put, defaultValue));
    }

    public Supplier<String> addString(
            String name,
            Supplier<String> valueSupplier) {
        return addString(name, valueSupplier, "");
    }

    public Supplier<boolean[]> addBooleanArray(
            String name,
            Supplier<boolean[]> valueSupplier) {
        return registerField(new LogField<>(name, valueSupplier, LogTable::get, LogTable::put, new boolean[0]));
    }

    public Supplier<long[]> addIntegerArray(
            String name,
            Supplier<long[]> valueSupplier) {
        return registerField(new LogField<>(name, valueSupplier, LogTable::get, LogTable::put, new long[0]));
    }

    public Supplier<double[]> addDoubleArray(
            String name,
            Supplier<double[]> valueSupplier) {
        return registerField(new LogField<>(name, valueSupplier, LogTable::get, LogTable::put, new double[0]));
    }

    public Supplier<String[]> addStringArray(
            String name,
            Supplier<String[]> valueSupplier) {
        return registerField(new LogField<>(name, valueSupplier, LogTable::get, LogTable::put, new String[0]));
    }

    public Supplier<boolean[][]> addBooleanMatrix(
            String name,
            Supplier<boolean[][]> valueSupplier) {
        return registerField(new LogField<>(name, valueSupplier, LogTable::get, LogTable::put, new boolean[0][0]));
    }

    public Supplier<long[][]> addLongMatrix(
            String name,
            Supplier<long[][]> valueSupplier) {
        return registerField(new LogField<>(name, valueSupplier, LogTable::get, LogTable::put, new long[0][0]));
    }

    public Supplier<double[][]> addDoubleMatrix(
            String name,
            Supplier<double[][]> valueSupplier) {
        return registerField(new LogField<>(name, valueSupplier, LogTable::get, LogTable::put, new double[0][0]));
    }

    public Supplier<String[][]> addStringMatrix(
            String name,
            Supplier<String[][]> valueSupplier) {
        return registerField(new LogField<>(name, valueSupplier, LogTable::get, LogTable::put, new String[0][0]));
    }

    public <T extends WPISerializable> Supplier<T> addObject(
            String name,
            Supplier<T> valueSupplier,
            T defaultValue) {
        return registerField(new LogField<>(name, valueSupplier, LogTable::get, LogTable::put, defaultValue));
    }

    @SuppressWarnings("unchecked")
    public <T extends StructSerializable> Supplier<T[]> addObjectArray(
            String name,
            Supplier<T[]> valueSupplier,
            T[] defaultValue) {
        return registerField(new LogField<>(name, valueSupplier, LogTable::get, LogTable::put, defaultValue));
    }

    public <T extends StructSerializable> Supplier<T[][]> addObjectMatrix(
            String name,
            Supplier<T[][]> valueSupplier,
            T[][] defaultValue) {
        return registerField(new LogField<>(name, valueSupplier, LogTable::get, LogTable::put, defaultValue));
    }

    public <R extends Record> Supplier<R> addRecord(
            String name,
            Supplier<R> valueSupplier,
            R defaultValue) {
        return registerField(new LogField<>(name, valueSupplier, LogTable::get, LogTable::put, defaultValue));
    }

    @SuppressWarnings("unchecked")
    public <R extends Record> Supplier<R[]> addRecordArray(
            String name,
            Supplier<R[]> valueSupplier,
            R[] defaultValue) {
        return registerField(new LogField<>(name, valueSupplier, LogTable::get, LogTable::put, defaultValue));
    }

    public <R extends Record> Supplier<R[][]> addRecordMatrix(
            String name,
            Supplier<R[][]> valueSupplier,
            R[][] defaultValue) {
        return registerField(new LogField<>(name, valueSupplier, LogTable::get, LogTable::put, defaultValue));
    }

    public <U extends Unit, M extends Measure<U>> Supplier<M> addMeasure(
            String name,
            Supplier<M> valueSupplier,
            M defaultValue) {
        return registerField(new LogField<>(name, valueSupplier, LogTable::getMeasure, LogTable::putMeasure, defaultValue));
    }

    public <T> Supplier<T> add(String name, Supplier<T> supplier,
           LogField.LogTableGetFunc<T> getter,
           LogField.LogTablePutFunc<T> putter, T defaultValue) {
       return registerField(new LogField<>(name, supplier, getter, putter, defaultValue));
    }

    public void recordOutput(String name, byte value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, byte[] value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, byte[][] value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, boolean value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, boolean[] value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, boolean[][] value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, int value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, int[] value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, int[][] value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, long value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, long[] value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, long[][] value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, float value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, float[] value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, float[][] value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, double value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, double[] value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, double[][] value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, String value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, String[] value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, String[][] value) {
        Logger.recordOutput(prefix + name, value);
    }

    public <E extends Enum<E>> void recordOutput(String name, E value) {
        Logger.recordOutput(prefix + name, value);
    }

    @SuppressWarnings("unchecked")
    public <E extends Enum<E>> void recordOutput(String name, E... value) {
        Logger.recordOutput(prefix + name, value);
    }

    public <E extends Enum<E>> void recordOutput(String name, E[][] value) {
        Logger.recordOutput(prefix + name, value);
    }

    public <R extends Record> void recordOutput(String name, R value) {
        Logger.recordOutput(prefix + name, value);
    }

    @SuppressWarnings("unchecked")
    public <R extends Record> void recordOutput(String name, R... value) {
        Logger.recordOutput(prefix + name, value);
    }

    public <R extends Record> void recordOutput(String name, R[][] value) {
        Logger.recordOutput(prefix + name, value);
    }

    public <T extends WPISerializable> void recordOutput(String name, T value) {
        Logger.recordOutput(prefix + name, value);
    }

    @SuppressWarnings("unchecked")
    public <T extends StructSerializable> void recordOutput(String name, T... value) {
        Logger.recordOutput(prefix + name, value);
    }

    public <T extends StructSerializable> void recordOutput(String name, T[][] value) {
        Logger.recordOutput(prefix + name, value);
    }

    public void recordOutput(String name, LoggedMechanism2d value) {
        Logger.recordOutput(prefix + name, value);
    }

    public <U extends Unit> void recordOutputMeasure(String name, Measure<U> value) {
        Logger.recordOutputMeasure(prefix + name, value);
    }
}