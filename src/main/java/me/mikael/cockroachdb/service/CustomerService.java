package me.mikael.cockroachdb.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.mikael.cockroachdb.entities.Customer;
import me.mikael.cockroachdb.kafka.ActivityEventPublisher;
import me.mikael.cockroachdb.kafka.CustomerEventPublisher;
import me.mikael.cockroachdb.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerEventPublisher customerEventPublisher;
    private final ActivityEventPublisher activityEventPublisher;

    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    @Transactional("transactionManager")
    public Customer createCustomer(String name, LocalDate birthday) {
        log.info("Saving to DB and sending Events");
        var customer = customerRepository.save(
                Customer.builder()
                        .name(name)
                        .birthday(birthday)
                        .build());
        customerEventPublisher.publish(customer);
        activityEventPublisher.publish(customer);
        return customer;
    }
}