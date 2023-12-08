package net.remgant.camunda.models;

import java.util.List;
import java.util.Map;

public class Message {
    private String messageName;
    private Map<String,ProcessVariable> processVariables;

    public Message() {
    }

    public Message(String messageName, Map<String, ProcessVariable> processVariables) {
        this.messageName = messageName;
        this.processVariables = processVariables;
    }

    public String getMessageName() {
        return messageName;
    }

    public Map<String, ProcessVariable> getProcessVariables() {
        return processVariables;
    }
}
