package com.example.bank.service;

import com.example.bank.dao.CustomerRepository;
import com.example.bank.dto.CustomerDto;
import com.example.bank.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findCustomerByLogin(username);
        if (customer == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return customer;
    }

    public boolean saveUser(CustomerDto customer) {
        Customer userFromDB = customerRepository.findCustomerByLogin(customer.getLogin());
        if (userFromDB != null) {
            return false;
        }
        Customer newCustomer = new Customer();
        newCustomer.setLogin(customer.getLogin());
        newCustomer.setFirstName(customer.getFirstName());
        newCustomer.setLastName(customer.getLastName());
        newCustomer.setPatronymic(customer.getPatronymic());
        newCustomer.setPassword(bCryptPasswordEncoder.encode(customer.getPassword()));
        customerRepository.save(newCustomer);
        return true;
    }
}
