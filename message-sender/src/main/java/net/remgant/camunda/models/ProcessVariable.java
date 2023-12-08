package net.remgant.camunda.models;

import lombok.Getter;

@Getter
public class ProcessVariable {
    private String value;
    private String type;
    private ValueInfo valueInfo;

    @SuppressWarnings("unused")
    public ProcessVariable() {
    }

    public ProcessVariable(String value, String type, ValueInfo valueInfo) {
        this.value = value;
        this.type = type;
        this.valueInfo = valueInfo;
    }
}
