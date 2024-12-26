package me.mikael.cockroachdb.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.mikael.cockroachdb.entities.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.activity-topic-name}")
    private String activityTopicName;


    private final Random random = new Random();

    public void publish(Customer customer) {

        var shouldFail = random.nextInt(2) == 0;
        if (shouldFail) {
            log.info("FAILING deliberately");
            throw new RuntimeException("BOOM");
        }

        try {
            var content = String.format("{\"activity\":\"cust_created\",\"id\":\"%s\"}", customer.getId().toString());
            var completableFuture =kafkaTemplate.send(
                    activityTopicName,
                    customer.getId().toString(),
                    content);
            var sendResult =completableFuture.get();
            log.info("SEND result : {}", sendResult.toString());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
