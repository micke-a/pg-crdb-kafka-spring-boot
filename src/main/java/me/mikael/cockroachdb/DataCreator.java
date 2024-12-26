package me.mikael.cockroachdb;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import me.mikael.cockroachdb.entities.Customer;
import me.mikael.cockroachdb.repository.CustomerRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class DataCreator implements ApplicationRunner {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional("transactionManager")
    public void run(ApplicationArguments args) throws Exception {

        if (customerRepository.count() > 0) {
            return;
        }

        for (int i = 0; i < 50; i++) {
            customerRepository.save(
                    Customer.builder()
                            .name("customer " + i)
                            .birthday(LocalDate.now().minusYears( 10+i))
                            .build()
            );
        }

    }
}
