package team2679.atlantiskit.periodicalerts;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.Alert.AlertType;

public class PeriodicAlertsGroup {
    public static final PeriodicAlertsGroup defaultInstance = new PeriodicAlertsGroup("PeriodicAlerts");
    private final String groupName;

    public PeriodicAlertsGroup(String groupName) {
        this.groupName = groupName;
    }

    public PeriodicAlertsGroup getSubGroup(String subGroupName) {
        return new PeriodicAlertsGroup(groupName + "/" + subGroupName);
    }

    public BooleanSupplier addAlert(Supplier<String> message, BooleanSupplier isActive, AlertType alertType) {
        PeriodicAlert periodicAlert = new PeriodicAlert(this, message, isActive, alertType);
        PeriodicAlertsManager.add(periodicAlert);
        return isActive;
    }

    public BooleanSupplier addInfoAlert(Supplier<String> message, BooleanSupplier isActive) {
        return addAlert(message, isActive, AlertType.kInfo);
    }

    public BooleanSupplier addWarningAlert(Supplier<String> message, BooleanSupplier isActive) {
        return addAlert(message, isActive, AlertType.kWarning);
    }

    public BooleanSupplier addErrorAlert(Supplier<String> message, BooleanSupplier isActive) {
        return addAlert(message, isActive, AlertType.kError);
    }

    public String getName() {
        return groupName;
    }
}