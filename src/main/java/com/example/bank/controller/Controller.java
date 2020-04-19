package com.example.bank.controller;

import com.example.bank.dto.CardDto;
import com.example.bank.entity.Customer;
import com.example.bank.exception.IllegalCardIdPassed;
import com.example.bank.service.CardsService;
import com.example.bank.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class Controller {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CardsService cardsService;

    private final CustomerService customerService;

    public Controller(CardsService cardsService, CustomerService customerService) {
        this.cardsService = cardsService;
        this.customerService = customerService;
    }

    private Customer getAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Customer)auth.getPrincipal();
    }

    @GetMapping("/cards")
    public List<CardDto> getAllCards(@RequestHeader Map<String, String> headers) {
        logger.info(headers.entrySet().stream().map(entry -> entry.getKey() + " = " + entry.getValue())
                .collect(Collectors.joining("\n")));
        Customer customer = getAuthenticated();
        Customer fromBase = customerService.getCustomerById(customer.getCustomerId());
        List<CardDto> tasksList;
        tasksList = cardsService.mapCardsListToDto(fromBase.getCardSet());
        return tasksList;
    }

    @PutMapping("/transfer/{cardIdFrom}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transferToClient(@PathVariable Long cardIdFrom,
                                 @RequestParam("card_id_to") Long cardIdTo,
                                 @RequestParam("amount") Long amount) {
        Customer customerFrom = getAuthenticated();
        customerFrom.getCardSet().stream()
                .filter(card1 -> card1.getCardId().equals(cardIdFrom))
                .findAny()
                .orElseThrow(() -> new IllegalCardIdPassed(cardIdFrom + " is not your card"));
        cardsService.transferMoney(cardIdFrom, cardIdTo, amount);
    }

    @PutMapping("/deposit/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deposit(@PathVariable Long cardId,
                        @RequestParam("amount") Long amount) {
        Customer customerFrom = getAuthenticated();
        customerFrom.getCardSet().stream()
                .filter(card1 -> card1.getCardId().equals(cardId))
                .findAny()
                .orElseThrow(() -> new IllegalCardIdPassed(cardId + " is not your card"));
        cardsService.deposit(cardId, amount);
    }
}
