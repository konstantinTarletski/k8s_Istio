package askend.example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
class UniversalController {

    private static final String TARGET_ENDPOINT = "/get-hello";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final RestClient restClient = RestClient.create();

    private final AppCommunication appCommunication;

    @Value("${spring.application.name}")
    private String role;

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(formatter);
    }

    @GetMapping("/get-hello")
    public Map<String, String> getHello() {
        return Map.of(
                "role", role,
                "timestamp", getCurrentTimestamp()
        );
    }

    @GetMapping("/get-communication-hello")
    public List<Object> getCommunicationHello() {
        List<Object> finalResponseList = new ArrayList<>();
        finalResponseList.add(getHello());

        for (String host : appCommunication.getHosts()) {

            try {
                String fullUrl = host + TARGET_ENDPOINT;
                log.info("Trying to connect to raw host: {}", fullUrl);

                Map<?, ?> neighborResponse = restClient.get()
                        .uri(fullUrl)
                        .retrieve()
                        .body(Map.class);

                if (neighborResponse != null) {
                    finalResponseList.add(Map.of(
                            "host", host,
                            "response-get-hello", neighborResponse
                    ));
                }
            } catch (Exception e) {
                log.error("Error connecting to {}, message is {}", host, e.getMessage());
                finalResponseList.add(Map.of(
                        "host", host,
                        "response-get-hello", Map.of("error", "Failed to reach host: " + e.getMessage())
                ));
            }
        }

        return finalResponseList;
    }

}