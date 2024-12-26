package me.mikael.cockroachdb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.mikael.cockroachdb.entities.Customer;
import me.mikael.cockroachdb.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/list")
    public List<Customer> getCustomers() {
        return customerService.getCustomers();
    }

    @GetMapping("/create")
    public ResponseEntity<Customer> create(
            String name,
            LocalDate birthday
            ) {
        log.info("Creating customer {} with birthday {}", name, birthday);
        var cust = customerService.createCustomer(name, birthday);
        return ResponseEntity.ok(cust);
    }
}
