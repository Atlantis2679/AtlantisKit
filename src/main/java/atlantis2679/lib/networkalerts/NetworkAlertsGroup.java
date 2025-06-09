package atlantis2679.lib.networkalerts;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.Alert.AlertType;

public class NetworkAlertsGroup {
    public static final NetworkAlertsGroup defaultInstance = new NetworkAlertsGroup("NetworkAlerts");
    private final String groupName;

    public NetworkAlertsGroup(String groupName) {
        this.groupName = groupName;
    }

    public NetworkAlertsGroup getSubGroup(String subGroupName) {
        return new NetworkAlertsGroup(groupName + "/" + subGroupName);
    }

    public BooleanSupplier addAlert(Supplier<String> message, BooleanSupplier isActive, AlertType alertType) {
        NetworkPeriodicAlert periodicAlert = new NetworkPeriodicAlert(this, message, isActive, alertType);
        NetworkAlertsManager.addNetworkPeriodicAlert(periodicAlert);
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