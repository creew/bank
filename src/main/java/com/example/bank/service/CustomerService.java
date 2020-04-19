package com.example.bank.service;

import com.example.bank.dao.CustomerRepository;
import com.example.bank.entity.Customer;
import com.example.bank.exception.IllegalArgumentsPassed;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentsPassed("No customer with id " + id + " found"));
    }
}
