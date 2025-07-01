package atlantis2679.lib.logfields.logfields;

import java.util.function.DoubleSupplier;

import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

public class DoubleLogField implements DoubleSupplier, LoggableInputs {
    private final DoubleSupplier valueSupplier;
    private final String name;
    private double value;

    public DoubleLogField(
            String name,
            DoubleSupplier valueSupplier,
            double defaultValue) {
        this.name = name;
        this.valueSupplier = valueSupplier;
        this.value = defaultValue;
    }

    @Override
    public double getAsDouble() {
        return value;
    }

    @Override
    public void toLog(LogTable table) {
        value = valueSupplier.getAsDouble();
        table.put(name, value);
    }

    @Override
    public void fromLog(LogTable table) {
        value = table.get(name, value);
    }
}
