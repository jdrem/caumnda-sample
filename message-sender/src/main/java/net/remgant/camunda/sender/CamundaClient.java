package net.remgant.camunda.sender;

import feign.Headers;
import feign.RequestLine;
import net.remgant.camunda.models.Message;

public interface CamundaClient {
    @RequestLine("POST /engine-rest/message")
    @Headers("Content-Type: application/json")
    void sendMessage(Message message);
}
