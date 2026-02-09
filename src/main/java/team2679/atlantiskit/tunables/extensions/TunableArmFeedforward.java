package team2679.atlantiskit.tunables.extensions;

import edu.wpi.first.math.controller.ArmFeedforward;
import team2679.atlantiskit.tunables.SendableType;
import team2679.atlantiskit.tunables.Tunable;
import team2679.atlantiskit.tunables.TunableBuilder;

public class TunableArmFeedforward extends ArmFeedforward implements Tunable {
    public TunableArmFeedforward(double ks, double kg, double kv, double ka, double dtSeconds) {
        super(ks, kg, kv, ka, dtSeconds);
    }

    public TunableArmFeedforward(double ks, double kg, double kv, double ka) {
        super(ks, kg, kv, ka);
    }

    public TunableArmFeedforward(double ks, double kg, double kv) {
        super(ks, kg, kv);
    }


    @Override
    public void initTunable(TunableBuilder builder) {
        builder.setSendableType(SendableType.LIST);
        builder.addDoubleProperty("kS", this::getKs, this::setKs);
        builder.addDoubleProperty("kG", this::getKg, this::setKg);
        builder.addDoubleProperty("kV", this::getKv, this::setKv);
        builder.addDoubleProperty("kA", this::getKa, this::setKa);
    }

}
