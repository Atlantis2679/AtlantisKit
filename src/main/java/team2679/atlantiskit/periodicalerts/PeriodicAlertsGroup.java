package team2679.atlantiskit.periodicalerts;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.wpilib.util.Alert;

public class PeriodicAlertsGroup {
    public static final PeriodicAlertsGroup defaultInstance = new PeriodicAlertsGroup("PeriodicAlerts");
    private final String groupName;
    private int nextId = 0;

    public PeriodicAlertsGroup(String groupName) {
        this.groupName = groupName;
    }

    public PeriodicAlertsGroup getSubGroup(String subGroupName) {
        return new PeriodicAlertsGroup(groupName + "/" + subGroupName);
    }

    public BooleanSupplier addAlert(Supplier<String> message, BooleanSupplier isActive, Alert.Level alertLevel) {
        Integer id = nextId++;
        PeriodicAlert periodicAlert = new PeriodicAlert(this, message, isActive, new Alert(this.getName(), id.toString(), message.get(), alertLevel));
        PeriodicAlertsManager.add(periodicAlert);
        return isActive;
    }

    public BooleanSupplier addLowAlert(Supplier<String> message, BooleanSupplier isActive) {
        return addAlert(message, isActive, Alert.Level.LOW);
    }

    public BooleanSupplier addMediumAlert(Supplier<String> message, BooleanSupplier isActive) {
        return addAlert(message, isActive, Alert.Level.MEDIUM);
    }

    public BooleanSupplier addHighAlert(Supplier<String> message, BooleanSupplier isActive) {
        return addAlert(message, isActive, Alert.Level.HIGH);
    }

    public String getName() {
        return groupName;
    }
}