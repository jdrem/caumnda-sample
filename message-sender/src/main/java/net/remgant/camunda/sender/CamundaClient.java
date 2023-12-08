package net.remgant.camunda.sender;

import net.remgant.camunda.models.Message;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name ="camunda", url="${feign.url}")
public interface CamundaClient {
    @RequestMapping(method = RequestMethod.POST, value="engine-rest/message", consumes = "application/json")
    void sendMessage(Message message);
}
