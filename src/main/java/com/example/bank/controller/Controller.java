package com.example.bank.controller;

import com.example.bank.dao.CardRepository;
import com.example.bank.dao.CustomerRepository;
import com.example.bank.dto.CardDto;
import com.example.bank.entity.Customer;
import com.example.bank.service.CardsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class Controller {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private CustomerRepository customerRepository;

    private CardRepository cardRepository;

    private CardsService cardsService;

    public Controller(CustomerRepository customerRepository, CardRepository cardRepository, CardsService cardsService) {
        this.customerRepository = customerRepository;
        this.cardRepository = cardRepository;
        this.cardsService = cardsService;
    }

    @GetMapping("/cards")
    public List<CardDto> getAllCards(@RequestHeader Map<String, String> headers) {
        logger.info(headers.entrySet().stream().map(entry -> entry.getKey() + " = " + entry.getValue())
                .collect(Collectors.joining("\n")));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Customer customer = (Customer)auth.getPrincipal();
        Customer fromBase = customerRepository.getOne(customer.getCustomerId());
        List<CardDto> tasksList;
        tasksList = cardsService.mapCardsListToDto(fromBase.getCardSet());
        return tasksList;
    }
}
