package team2679.atlantiskit.tunables.sendableproperties;

import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.wpi.first.util.sendable.SendableBuilder;
import team2679.atlantiskit.logfields.LogFieldsTable;

public class StringTunableProperty extends TunableProperty {
    private final Supplier<String[]> field;
    private String[] valueFromNetwork = {};
    private final Consumer<String> setter;

    public StringTunableProperty(
            String key,
            Supplier<String> getter,
            Consumer<String> setter,
            LogFieldsTable fieldsTable,
            SendableBuilder sendableBuilder) {
        this.setter = setter;

        field = fieldsTable.addStringArray(key, () -> {
            String[] newValue = valueFromNetwork;
            valueFromNetwork = new String[] {};
            return newValue;
        });
        fieldsTable.update();

        sendableBuilder.addStringProperty(key, getter, value -> valueFromNetwork = new String[] { value });
    }

    @Override
    protected void updateSetter() {
        if (field.get().length != 0) {
            setter.accept(field.get()[0]);
        }
    }
}
