package atlantis2679.lib.tunables;

public enum SendableType {
    NONE(null),
    LIST("LW Subsystem"),
    PIDController("PIDController");
    
    private String stringType;

    private SendableType(String stringType) {
        this.stringType = stringType;
    }

    public String getStringType() {
        return stringType;
    }
}    