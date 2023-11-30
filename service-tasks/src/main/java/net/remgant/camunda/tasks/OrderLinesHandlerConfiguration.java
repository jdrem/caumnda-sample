package net.remgant.camunda.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.remgant.camunda.models.Order;
import net.remgant.camunda.models.OrderLine;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.spring.boot.starter.ClientProperties;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class OrderLinesHandlerConfiguration {
    protected String workerId;

    public OrderLinesHandlerConfiguration(ClientProperties properties) {
        workerId = properties.getWorkerId();
    }

    @ExternalTaskSubscription("validate-order-line")
    @Bean
    public ExternalTaskHandler validateOrderLine() {
        return (externalTask, externalTaskService) -> {
            String orderLineString = externalTask.getVariable("OrderLine");
            ObjectMapper objectMapper = new ObjectMapper();
            OrderLine orderLine;
            try {
                orderLine = objectMapper.readValue(orderLineString, OrderLine.class);
            } catch (JsonProcessingException e) {
                log.error("mapping object", e);
                Map<String, Object> variables = Map.of("OrderLineValid", false);
                externalTaskService.complete(externalTask, variables);
                return;
            }
            log.info("Validated order line: {}", orderLine);

            Map<String, Object> variables = Map.of("OrderLineValid", true);
            externalTaskService.complete(externalTask, variables);
        };

    }

    @ExternalTaskSubscription("send-order-line")
    @Bean
    public ExternalTaskHandler sendOrderLine() {

        return (externalTask, externalTaskService) -> {
            String orderLineString = externalTask.getVariable("OrderLine");
            ObjectMapper objectMapper = new ObjectMapper();
            OrderLine orderLine;
            try {
                orderLine = objectMapper.readValue(orderLineString, OrderLine.class);
            } catch (JsonProcessingException e) {
                log.error("mapping object", e);
                Map<String, Object> variables = Map.of("OrderLineValid", false);
                externalTaskService.complete(externalTask, variables);
                return;
            }
            log.info("Sending order line to external source: {}", orderLine);
            orderLine.setCompleted(true);
            externalTaskService.complete(externalTask, Map.of("OrderLine", orderLine));
        };

    }

    @ExternalTaskSubscription("finish-order-lines")
    @Bean
    public ExternalTaskHandler finishOrderLines() {

        return (externalTask, externalTaskService) -> {
            List<String> orderMsgList = externalTask.getVariable("OrderMessageList");
            log.info("Finished order: {}", orderMsgList);
            externalTaskService.complete(externalTask);
        };

    }
}
