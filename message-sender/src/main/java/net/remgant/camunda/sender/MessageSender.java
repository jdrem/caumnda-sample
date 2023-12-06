package net.remgant.camunda.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

        Map<String, Object> body1 =  Map.of("messageName", "order-message",
                "processVariables",
                Map.of("OrderMessageList", Map.of("value", orderLinesListStr, "type", "Object",
                                "valueInfo", Map.of("objectTypeName", "java.util.List<java.lang.String>",
                                        "serializationDataFormat", "application/json"))
                ));
        @SuppressWarnings("rawtypes")
        Mono<Map> result = WebClient.create()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme(scheme)
                        .host(host)
                        .port(port)
                        .path("engine-rest/message")
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(body1))
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK) || response.statusCode().equals(HttpStatus.NO_CONTENT))
                        return response.bodyToMono(Map.class);
                    else
                        throw new RuntimeException("error sending message: " + response.statusCode());
                });
        @SuppressWarnings("unchecked")
        Map<String, Object> latestResult = result.block();
        if (latestResult != null)
            log.info("Latest result: {}", latestResult);

    }
}
