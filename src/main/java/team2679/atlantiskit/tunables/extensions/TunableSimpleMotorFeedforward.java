package team2679.atlantiskit.tunables.extensions;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import team2679.atlantiskit.tunables.SendableType;
import team2679.atlantiskit.tunables.Tunable;
import team2679.atlantiskit.tunables.TunableBuilder;

public class TunableSimpleMotorFeedforward extends SimpleMotorFeedforward implements Tunable {
    public TunableSimpleMotorFeedforward(double ks, double kv, double ka, double dtSeconds) {
        super(ks, kv, ka, dtSeconds);
    }

    public TunableSimpleMotorFeedforward(double ks, double kv, double ka) {
        super(ks, kv, ka);
    }

    public TunableSimpleMotorFeedforward(double ks, double kv) {
        super(ks, kv);
    }

    @Override
    public void initTunable(TunableBuilder builder) {
        builder.setSendableType(SendableType.LIST);
        builder.addDoubleProperty("kS", this::getKs, this::setKs);
        builder.addDoubleProperty("kV", this::getKa, this::setKv);
        builder.addDoubleProperty("kA", this::getKa, this::setKa);
    }
}
