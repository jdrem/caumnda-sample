package net.remgant.camunda.tasks;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.spring.boot.starter.ClientProperties;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.BooleanValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class HandlerConfiguration {
    private final static Logger log = LoggerFactory.getLogger(HandlerConfiguration.class);
    protected String workerId;

    public HandlerConfiguration(ClientProperties properties) {
        workerId = properties.getWorkerId();
    }

    @ExternalTaskSubscription("process-order")
    @Bean
    public ExternalTaskHandler processOrder() {
        return (externalTask, externalTaskService) -> {
            String key1 = externalTask.getVariable("key1");
            String key2 = externalTask.getVariable("key2");
            log.info("{} {} key1={}, key2={}", workerId, externalTask.getId(), key1, key2);
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
