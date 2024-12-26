package me.mikael.cockroachdb.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.mikael.cockroachdb.entities.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.customer-topic-name}")
    private String customerTopicName;

    public void publish(Customer customer) {

        try {
            var content = objectMapper.writeValueAsString(customer);
            var completableFuture =kafkaTemplate.send(
                    customerTopicName,
                    customer.getId().toString(),
                    content);
            var sendResult =completableFuture.get();
            log.info("SEND result : {}", sendResult.toString());
        } catch (JsonProcessingException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
