package net.remgant.camunda.tasks;

import net.remgant.camunda.models.Order;
import net.remgant.camunda.models.OrderLine;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.spring.boot.starter.ClientProperties;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration

public class OrderLinesHandlerConfiguration {
    private final static Logger log = LoggerFactory.getLogger(OrderLinesHandlerConfiguration.class);
    protected String workerId;

    public OrderLinesHandlerConfiguration(ClientProperties properties) {
        workerId = properties.getWorkerId();
    }

    @ExternalTaskSubscription("validate-order-line")
    @Bean
    public ExternalTaskHandler validateOrderLine() {
        return (externalTask, externalTaskService) -> {
            OrderLine orderLine = externalTask.getVariable("OrderLine");
            log.info("Validated order line: {}", orderLine);

            Map<String, Object> variables = Map.of("OrderLineValid", true);
            externalTaskService.complete(externalTask, variables);
        };

    }

    @ExternalTaskSubscription("send-order-line")
    @Bean
    public ExternalTaskHandler sendOrderLine() {

        return (externalTask, externalTaskService) -> {
            OrderLine orderLine = externalTask.getVariable("OrderLine");
            log.info("Sending order line to external source: {}", orderLine);
            orderLine.setCompleted(true);
            externalTaskService.complete(externalTask, Map.of("OrderLine", orderLine));
        };

    }

    @ExternalTaskSubscription("finish-order-lines")
    @Bean
    public ExternalTaskHandler finishOrderLines() {

        return (externalTask, externalTaskService) -> {
            Order order = externalTask.getVariable("OrderMessage");
            log.info("Finished order: {}", order);
            externalTaskService.complete(externalTask);
        };

    }
}
