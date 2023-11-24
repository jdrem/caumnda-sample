package net.remgant.camunda.sender;

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

import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

@SpringBootApplication
public class MessageSender implements CommandLineRunner {
    private final static Logger log = LoggerFactory.getLogger(MessageSender.class);
    String scheme = "http";
    String host = "localhost";
    int port = 8080;
    String processKey = "ProcessOrder";

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
        Map<String, Object> body = Map.of("businessKey", businessKey,
                "variables",
                Map.of("key1", Map.of("value", randomString.get(), "type", "String"),
                        "key2", Map.of("value", randomString.get(), "type", "String")));
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
