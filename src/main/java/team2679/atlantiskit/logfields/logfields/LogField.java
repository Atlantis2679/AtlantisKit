package team2679.atlantiskit.logfields.logfields;

import java.util.function.Supplier;

import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

public class LogField<T> implements Supplier<T>, LoggableInputs {
    private final Supplier<T> valueSupplier;
    private final String name;
    private final LogTableGetFunc<T> logTableGetFunc;
    private final LogTablePutFunc<T> logTablePutFunc;
    private T value;

    public interface LogTablePutFunc<T> {
        void putInTable(LogTable table, String name, T value);
    }

    public interface LogTableGetFunc<T> {
        T getFromTable(LogTable table, String name, T defaultValue);
    }

    public LogField(
            String name,
            Supplier<T> valueSupplier,
            LogTableGetFunc<T> logTableGetFunc,
            LogTablePutFunc<T> logTablePutFunc) {
        this.name = name;
        this.valueSupplier = valueSupplier;
        this.logTableGetFunc = logTableGetFunc;
        this.logTablePutFunc = logTablePutFunc;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void toLog(LogTable table) {
        value = valueSupplier.get();
        logTablePutFunc.putInTable(table, name, value);
    }

    @Override
    public void fromLog(LogTable table) {
        value = logTableGetFunc.getFromTable(table, name, value);
    }
}
