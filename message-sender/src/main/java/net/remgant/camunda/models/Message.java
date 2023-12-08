package net.remgant.camunda.models;

import lombok.Getter;

import java.util.Map;

@Getter
public class Message {
    private String messageName;
    private Map<String,ProcessVariable> processVariables;

    @SuppressWarnings("unused")
    public Message() {
    }

    public Message(String messageName, Map<String, ProcessVariable> processVariables) {
        this.messageName = messageName;
        this.processVariables = processVariables;
    }
}
