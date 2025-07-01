package team2679.atlantiskit.logfields;

public abstract class IOBase {
    protected final LogFieldsTable fields;

    protected IOBase(LogFieldsTable fieldsTable) {
        fields = fieldsTable;
        fieldsTable.setPeriodicBeforeFields(this::periodicBeforeFields);
    }

    protected IOBase(String name) {
        fields = new LogFieldsTable(name);
    }

    public LogFieldsTable getFieldsTable() {
        return fields;
    }

    protected void periodicBeforeFields() {
    }
}