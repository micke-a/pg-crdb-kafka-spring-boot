package me.mikael.cockroachdb.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.mikael.cockroachdb.entities.Customer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppEventListener {

    private final ObjectMapper objectMapper;

    @Transactional("kafkaTransactionManager")
    @KafkaListener(topics = "${app.kafka.customer-topic-name}", groupId = "${spring.application.name}-group")
    public void onCustomerMessage(String message) {

        try {
            var customer = objectMapper.readValue(message, Customer.class);
            log.info("RECEIVED customer: {}", customer);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional("kafkaTransactionManager")
    @KafkaListener(topics = "${app.kafka.activity-topic-name}", groupId = "${spring.application.name}-group")
    public void onActivityMessage(String message) {

        log.info("RECEIVED activity: {}", message);

    }
}
