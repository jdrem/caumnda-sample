package net.remgant.camunda.models;

public class ProcessVariable {
    private String value;
    private String type;
    private ValueInfo valueInfo;

    public ProcessVariable() {
    }

    public ProcessVariable(String value, String type, ValueInfo valueInfo) {
        this.value = value;
        this.type = type;
        this.valueInfo = valueInfo;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public ValueInfo getValueInfo() {
        return valueInfo;
    }
}
