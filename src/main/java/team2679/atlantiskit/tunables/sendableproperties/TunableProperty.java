package team2679.atlantiskit.tunables.sendableproperties;

import java.util.ArrayList;
import java.util.List;

public abstract class TunableProperty {
    private static List<TunableProperty> createdProperties = new ArrayList<>();
    
    public TunableProperty() {
        createdProperties.add(this);
    }

    public static void updateAll() {
        for (TunableProperty property : createdProperties) {
            property.updateSetter();
        }
    }

    protected abstract void updateSetter();
}