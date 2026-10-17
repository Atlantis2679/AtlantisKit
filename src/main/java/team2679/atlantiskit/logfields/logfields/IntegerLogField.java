package team2679.atlantiskit.logfields.logfields;

import java.util.function.IntSupplier;

import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

public class IntegerLogField implements IntSupplier, LoggableInputs {
    private final IntSupplier valueSupplier;
    private final String name;
    private int value;

    public IntegerLogField(
            String name,
            IntSupplier valueSupplier,
            int defaultValue) {
        this.name = name;
        this.valueSupplier = valueSupplier;
        this.value = defaultValue;
    }

    @Override
    public int getAsInt() {
        return value;
    }

    @Override
    public void toLog(LogTable table) {
        value = valueSupplier.getAsInt();
        table.put(name, value);
    }

    @Override
    public void fromLog(LogTable table) {
        value = table.get(name, value);
    }
}
