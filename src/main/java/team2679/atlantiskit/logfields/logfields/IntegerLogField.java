package atlantis2679.lib.logfields.logfields;

import java.util.function.LongSupplier;

import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

public class IntegerLogField implements LongSupplier, LoggableInputs {
    private final LongSupplier valueSupplier;
    private final String name;
    private long value;

    public IntegerLogField(
            String name,
            LongSupplier valueSupplier,
            long defaultValue) {
        this.name = name;
        this.valueSupplier = valueSupplier;
        this.value = defaultValue;
    }

    @Override
    public long getAsLong() {
        return value;
    }

    @Override
    public void toLog(LogTable table) {
        value = valueSupplier.getAsLong();
        table.put(name, value);
    }

    @Override
    public void fromLog(LogTable table) {
        value = table.get(name, value);
    }
}
