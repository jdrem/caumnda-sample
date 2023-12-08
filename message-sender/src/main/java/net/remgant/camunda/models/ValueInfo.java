package net.remgant.camunda.models;

public class ValueInfo {
    private String objectTypeName;
    private String serializationDataFormat;

    public ValueInfo() {
    }

    public ValueInfo(String objectTypeName, String serializationDataFormat) {
        this.objectTypeName = objectTypeName;
        this.serializationDataFormat = serializationDataFormat;
    }

    public String getObjectTypeName() {
        return objectTypeName;
    }

    public String getSerializationDataFormat() {
        return serializationDataFormat;
    }
}
