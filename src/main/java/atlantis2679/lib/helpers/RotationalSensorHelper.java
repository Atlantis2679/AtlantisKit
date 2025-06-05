package atlantis2679.lib.helpers;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import atlantis2679.lib.tunables.Tunable;
import atlantis2679.lib.tunables.TunableBuilder;
import atlantis2679.lib.valueholders.DoubleHolder;

public class RotationalSensorHelper implements Tunable {
    private double measuredAngle;
    private double offset;
    private double previousAngle;
    private double currTimeSec;
    private double prevTimeSec;
    private double calculatedAngle;

    private boolean continousWrapEnabled;
    private double continousWrapLowerBound;
    private double fullRotation;

    public RotationalSensorHelper(double initialMeasuredAngle, double initialOffset) {
        measuredAngle = initialMeasuredAngle;
        offset = initialOffset;
        prevTimeSec = Timer.getFPGATimestamp();
        recalculateAngle();
    }

    public RotationalSensorHelper(double initalMeasuredAngle) {
        this(initalMeasuredAngle, 0);
    }

    public void update(double measuredAngle) {
        this.measuredAngle = measuredAngle;
        prevTimeSec = currTimeSec;
        currTimeSec = Timer.getFPGATimestamp();
        previousAngle = calculatedAngle;
        recalculateAngle();
    }

    public void recalculateAngle() {
        calculatedAngle = measuredAngle - offset;
        if (continousWrapEnabled) {
            calculatedAngle = warpAngle(calculatedAngle);
        }
    }

    private double warpAngle(double angle) {
        return ((angle - continousWrapLowerBound) % fullRotation + fullRotation) % fullRotation + continousWrapLowerBound;
    }

    public void resetAngle(double newAngle) {
        setOffset(measuredAngle - newAngle);
    }

    public void enableContinuousWrap(double lowerBound, double upperBound) {
        continousWrapEnabled = true;
        continousWrapLowerBound = lowerBound;
        this.fullRotation = upperBound - lowerBound;
        recalculateAngle();
    }

    public void setOffset(double offset) {
        previousAngle += this.offset - offset;
        if(continousWrapEnabled)
            previousAngle = warpAngle(previousAngle);
        this.offset = offset;
        recalculateAngle();
    }

    public double getAngle() {
        return calculatedAngle;
    }

    public double getMeasuredAngle() {
        return measuredAngle;
    }

    public double getVelocity() {
        double deltaTime = currTimeSec - prevTimeSec;
        if (deltaTime == 0) {
            DriverStation.reportWarning(
                    "You should not request velocity after no time passed (probably called in initial loop).", true);
            return 0;
        }
        return (getAngle() - previousAngle) / deltaTime;
    }

    public double getOffset() {
        return offset;
    }

    @Override
    public void initTunable(TunableBuilder builder) {
        DoubleHolder angleToResetDegrees = new DoubleHolder(0);
        builder.addDoubleProperty("raw angle measurment",
                this::getMeasuredAngle, null);

        builder.addDoubleProperty("calculated angle", this::getAngle, null);

        builder.addDoubleProperty("offset", this::getOffset,
                this::setOffset);

        builder.addDoubleProperty("angle to reset", angleToResetDegrees::get,
                angleToResetDegrees::set);

        builder.addChild("reset!", new InstantCommand(() -> {
            resetAngle(angleToResetDegrees.get());
        }).ignoringDisable(true));
    }
}