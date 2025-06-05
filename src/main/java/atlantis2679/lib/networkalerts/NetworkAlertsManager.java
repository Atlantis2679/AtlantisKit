package atlantis2679.lib.networkalerts;

import java.util.ArrayList;
import java.util.List;

public class NetworkAlertsManager {
    private static final List<NetworkPeriodicAlert> alerts = new ArrayList<>();
    
    /** Update all the periodic alerts. This should be called every robot periodic. */
    public static void update() {
        for(NetworkPeriodicAlert networkAlert : alerts) {
            networkAlert.update();
        }
    }

    public static void addNetworkPeriodicAlert(NetworkPeriodicAlert periodicAlert) {
        alerts.add(periodicAlert);
    }
}