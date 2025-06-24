package atlantis2679.lib.periodicalerts;

import java.util.ArrayList;
import java.util.List;

public class PeriodicAlertsManager {
    private static final List<PeriodicAlert> alerts = new ArrayList<>();
    
    /** Update all the periodic alerts. This should be called every robot periodic. */
    public static void update() {
        for(PeriodicAlert networkAlert : alerts) {
            networkAlert.update();
        }
    }

    public static void addNetworkPeriodicAlert(PeriodicAlert periodicAlert) {
        alerts.add(periodicAlert);
    }
}