package atlantis2679.lib.tunables.sendableproperties;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import edu.wpi.first.util.sendable.SendableBuilder;
import atlantis2679.lib.logfields.LogFieldsTable;

public class NumberTunableProperty extends TunableProperty {
    private final Supplier<double[]> field;
    private double[] valueFromNetwork = {};
    private final DoubleConsumer setter;

    public NumberTunableProperty(
            String key,
            DoubleSupplier getter,
            DoubleConsumer setter,
            LogFieldsTable fieldsTable,
            SendableBuilder sendableBuilder) {
        this.setter = setter;

        field = fieldsTable.addDoubleArray(key, () -> {
            double[] newValue = valueFromNetwork;
            valueFromNetwork = new double[] {};
            return newValue;
        });
        fieldsTable.update();

        sendableBuilder.addDoubleProperty(key, getter, value -> valueFromNetwork = new double[] { value });
    }

    @Override
    protected void updateSetter() {
        if (field.get().length != 0) {
            setter.accept(field.get()[0]);
        }
    }
}
