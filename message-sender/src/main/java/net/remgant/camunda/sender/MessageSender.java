package net.remgant.camunda.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.remgant.camunda.models.Message;
import net.remgant.camunda.models.OrderLine;
import net.remgant.camunda.models.ProcessVariable;
import net.remgant.camunda.models.ValueInfo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

@SpringBootApplication
@EnableFeignClients
@Slf4j
public class MessageSender implements CommandLineRunner {

    final private CamundaClient camundaClient;

    public MessageSender(CamundaClient camundaClient) {
        this.camundaClient = camundaClient;
    }

    Random random = new Random();
    Supplier<String> randomString = () -> random.ints(97, 122)
            .limit(8)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();

    public static void main(String[] args) {
        new SpringApplicationBuilder(MessageSender.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void run(String... args) throws Exception {
        List<OrderLine> orderLines = List.of(
           new OrderLine(randomString.get(), "door", 1, BigDecimal.valueOf(20.0)),
           new OrderLine(randomString.get(), "handle", 2, BigDecimal.valueOf(5.0)),
           new OrderLine(randomString.get(), "hinge", 2, BigDecimal.valueOf(2.50))
        );
        ObjectMapper objectMapper = new ObjectMapper();
        String orderLinesListStr = objectMapper.writeValueAsString(orderLines.stream().map(ol -> {
            try {
                return objectMapper.writeValueAsString(ol);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList());

        Message message = new Message("order-message", Map.of("OrderMessageList",
                new ProcessVariable(orderLinesListStr, "object",
                        new ValueInfo("java.util.List<java.lang.String>", "application/json"))));

        camundaClient.sendMessage(message);
    }
}
