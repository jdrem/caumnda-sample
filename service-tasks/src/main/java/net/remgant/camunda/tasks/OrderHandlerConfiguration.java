package net.remgant.camunda.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.remgant.camunda.models.Order;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.spring.boot.starter.ClientProperties;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class OrderHandlerConfiguration {
    private final static Logger log = LoggerFactory.getLogger(OrderHandlerConfiguration.class);
    protected String workerId;

    public OrderHandlerConfiguration(ClientProperties properties) {
        workerId = properties.getWorkerId();
    }

    @ExternalTaskSubscription("process-order")
    @Bean
    public ExternalTaskHandler processOrder() {
        return (externalTask, externalTaskService) -> {
            String key1 = externalTask.getVariable("key1");
            String key2 = externalTask.getVariable("key2");
            String orderMessageJson = externalTask.getVariable("orderMessage");
            orderMessageJson = orderMessageJson.replace("\\\"", "\"");
            ObjectMapper objectMapper = new ObjectMapper();
            Order order = null;
            try {
                order = objectMapper.readValue(orderMessageJson, Order.class);
            } catch (JsonProcessingException e) {
                log.error("coverting from json", e);
                externalTaskService.complete(externalTask, Map.of("OrderSucceeded", "failed"));
            }

            log.info("{} {} key1={}, key2={}, orderMessage={}", workerId, externalTask.getId(), key1, key2, order);
            boolean succeeded = key1.charAt(0) >= 'a' && key1.charAt(0) < 'n';
            log.info("Key1 is {}, task has {}", key1, succeeded ? "succeeded" : "failed");
            Map<String, Object> variables = Map.of("OrderSucceeded", succeeded);
            externalTaskService.complete(externalTask, variables);
        };
    }

    @ExternalTaskSubscription("handle-success")
    @Bean
    public ExternalTaskHandler handleSuccess() {
        return (externalTask, externalTaskService) -> {
            String key1 = externalTask.getVariable("key1");
            String key2 = externalTask.getVariable("key2");
            log.info("{} {} Succeeded for key1={}, key2={}", workerId, externalTask.getId(), key1, key2);
            externalTaskService.complete(externalTask);
        };
    }

    @ExternalTaskSubscription("handle-error")
    @Bean
    public ExternalTaskHandler handleFailure() {
        return (externalTask, externalTaskService) -> {
            String key1 = externalTask.getVariable("key1");
            String key2 = externalTask.getVariable("key2");
            log.info("{} {} Failed for key1={}, key2={}", workerId, externalTask.getId(), key1, key2);
            externalTaskService.complete(externalTask);
        };
    }
}
