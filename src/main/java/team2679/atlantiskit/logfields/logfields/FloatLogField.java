package team2679.atlantiskit.logfields.logfields;

import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

import edu.wpi.first.util.function.FloatSupplier;

public class FloatLogField implements FloatSupplier, LoggableInputs {
    private final FloatSupplier valueSupplier;
    private final String name;
    private float value;

    public FloatLogField(
            String name,
            FloatSupplier valueSupplier,
            float defaultValue) {
        this.name = name;
        this.valueSupplier = valueSupplier;
        this.value = defaultValue;
    }

    @Override
    public float getAsFloat() {
        return value;
    }

    @Override
    public void toLog(LogTable table) {
        value = valueSupplier.getAsFloat();
        table.put(name, value);
    }

    @Override
    public void fromLog(LogTable table) {
        value = table.get(name, value);
    }
}
