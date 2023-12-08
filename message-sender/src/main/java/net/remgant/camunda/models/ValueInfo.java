package net.remgant.camunda.models;

import lombok.Getter;

@Getter
public class ValueInfo {
    private String objectTypeName;
    private String serializationDataFormat;

    @SuppressWarnings("unused")
    public ValueInfo() {
    }

    public ValueInfo(String objectTypeName, String serializationDataFormat) {
        this.objectTypeName = objectTypeName;
        this.serializationDataFormat = serializationDataFormat;
    }
}
