package atlantis2679.lib.tunables.extensions;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import atlantis2679.lib.tunables.SendableType;
import atlantis2679.lib.tunables.Tunable;
import atlantis2679.lib.tunables.TunableBuilder;

public class TunableSimpleMotorFeedforward implements Tunable {
    private SimpleMotorFeedforward baseFeedforward;

    public TunableSimpleMotorFeedforward(double ks, double kv, double ka) {
        baseFeedforward = new SimpleMotorFeedforward(ks, kv, ka);
    }

    public double calculate(double velocity) {
        return baseFeedforward.calculate(velocity);
    }

    public double maxAchievableVelocity(double maxVoltage, double acceleration) {
        return baseFeedforward.maxAchievableAcceleration(maxVoltage, acceleration);
    }

    public double minAchievableVelocity(double maxVoltage, double acceleration) {
        return baseFeedforward.minAchievableAcceleration(maxVoltage, acceleration);
    }

    public double maxAchievableAcceleration(double maxVoltage, double velocity) {
        return baseFeedforward.maxAchievableAcceleration(maxVoltage, velocity);
    }

    public double minAchievableAcceleration(double maxVoltage, double velocity) {
        return baseFeedforward.minAchievableAcceleration(maxVoltage, velocity);
    }

    public void setKS(double newKS) {
        baseFeedforward = new SimpleMotorFeedforward(newKS, baseFeedforward.getKv(), baseFeedforward.getKa());
    }

    public void setKV(double newKV) {
        baseFeedforward = new SimpleMotorFeedforward(baseFeedforward.getKs(), newKV, baseFeedforward.getKa());
    }

    public void setKA(double newKA) {
        baseFeedforward = new SimpleMotorFeedforward(baseFeedforward.getKs(), baseFeedforward.getKa(), newKA);
    }

    public SimpleMotorFeedforward getArmFeedforward() {
        return baseFeedforward;
    }

    @Override
    public void initTunable(TunableBuilder builder) {
        builder.setSendableType(SendableType.LIST);
        builder.addDoubleProperty("kS", baseFeedforward::getKs, this::setKS);
        builder.addDoubleProperty("kV", baseFeedforward::getKv, this::setKV);
        builder.addDoubleProperty("kA", baseFeedforward::getKa, this::setKA);
    }
}
