package net.remgant.camunda.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.remgant.camunda.models.Order;
import net.remgant.camunda.models.OrderLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

@SpringBootApplication
public class MessageSender implements CommandLineRunner {
    private final static Logger log = LoggerFactory.getLogger(MessageSender.class);
    String scheme = "http";
    String host = "localhost";
    int port = 8080;
    String processKey = "ProcessOrderLines";

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
        String businessKey = String.format("busisness-key-%s", randomString.get());
        List<OrderLine> orderLines = List.of(
           new OrderLine(businessKey+"0001", "door", 1, BigDecimal.valueOf(20.0)),
           new OrderLine(businessKey+"0002", "handle", 2, BigDecimal.valueOf(5.0)),
           new OrderLine(businessKey+"0003", "hinge", 2, BigDecimal.valueOf(2.50))
        );
        Order order = new Order(businessKey, "Jones", BigDecimal.valueOf(35.0), orderLines);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(order);
        //json = json.replace("\"", "\\\"");
        Map<String, Object> body = Map.of("businessKey", businessKey,
                "variables",
                Map.of("key1", Map.of("value", randomString.get(), "type", "String"),
                        "key2", Map.of("value", randomString.get(), "type", "String"),
                        "OrderMessage", Map.of("value", json, "type", "Object",
//                                "valueInfo", Map.of("objectTypeName", "java.util.HashMap<java.lang.String,java.lang.Object>",
                                "valueInfo", Map.of("objectTypeName", "net.remgant.camunda.models.Order",

                                        "serializationDataFormat", "application/json"))));
        @SuppressWarnings("rawtypes")
        Mono<Map> result = WebClient.create()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme(scheme)
                        .host(host)
                        .port(port)
                        .path(String.format("engine-rest/process-definition/key/%s/start", processKey))
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(body))
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK))
                        return response.bodyToMono(Map.class);
                    else
                        throw new RuntimeException("error sending message: " + response.statusCode());
                });
        @SuppressWarnings("unchecked")
        Map<String, Object> latestResult = result.block();
        log.info("Latest result: {}", latestResult);

    }
}
