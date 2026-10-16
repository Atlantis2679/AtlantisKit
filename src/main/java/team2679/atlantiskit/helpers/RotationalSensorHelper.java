package team2679.atlantiskit.helpers;

import org.wpilib.system.Timer;

public class RotationalSensorHelper {
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
        prevTimeSec = Timer.getTimestamp();
        recalculateAngle();
    }

    public RotationalSensorHelper(double initalMeasuredAngle) {
        this(initalMeasuredAngle, 0);
    }

    public void update(double measuredAngle) {
        this.measuredAngle = measuredAngle;
        prevTimeSec = currTimeSec;
        currTimeSec = Timer.getTimestamp();
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
        return (getAngle() - previousAngle) / deltaTime;
    }

    public double getOffset() {
        return offset;
    }
}
