package atlantis2679.lib.logfields.logfields;

import java.util.function.BooleanSupplier;

import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

public class BooleanLogField implements BooleanSupplier, LoggableInputs {
    private final BooleanSupplier valueSupplier;
    private final String name;
    private boolean value;

    public BooleanLogField(
            String name,
            BooleanSupplier valueSupplier,
            boolean defaultValue) {
        this.name = name;
        this.valueSupplier = valueSupplier;
        this.value = defaultValue;
    }

    @Override
    public boolean getAsBoolean() {
        return value;
    }

    @Override
    public void toLog(LogTable table) {
        value = valueSupplier.getAsBoolean();
        table.put(name, value);
    }

    @Override
    public void fromLog(LogTable table) {
        value = table.get(name, value);
    }
}
