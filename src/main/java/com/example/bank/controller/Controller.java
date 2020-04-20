package com.example.bank.controller;

import com.example.bank.dto.CardDto;
import com.example.bank.entity.Card;
import com.example.bank.entity.User;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.exception.IllegalCardIdPassed;
import com.example.bank.service.CardsService;
import com.example.bank.service.UserService;
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

    private final UserService userService;

    public Controller(CardsService cardsService, UserService userService) {
        this.cardsService = cardsService;
        this.userService = userService;
    }

    private User getAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User)auth.getPrincipal();
    }

    @GetMapping("/cards")
    public List<CardDto> getAllCards(@RequestHeader Map<String, String> headers) {
        User user = getAuthenticated();
        User fromBase = userService.getUserById(user.getUserId());
        List<CardDto> tasksList;
        tasksList = cardsService.mapCardsListToDto(fromBase.getCardSet());
        return tasksList;
    }

    @PutMapping("/transfer/{cardIdFrom}")
    @ResponseStatus(HttpStatus.OK)
    public CardDto transferToClient(@PathVariable Long cardIdFrom,
                                 @RequestParam("card_id_to") Long cardIdTo,
                                 @RequestParam("amount") Long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentsPassed("Amount less than or equal zero");
        }
        User userFrom = getAuthenticated();
        userFrom.getCardSet().stream()
                .filter(card1 -> card1.getCardId().equals(cardIdFrom))
                .findAny()
                .orElseThrow(() -> new IllegalCardIdPassed(cardIdFrom + " is not your card"));
        Card updated = cardsService.transferMoney(cardIdFrom, cardIdTo, amount);
        return cardsService.mapCardToCardDto(updated);
    }

    @PutMapping("/deposit/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deposit(@PathVariable Long cardId,
                        @RequestParam("amount") Long amount) {
        User userFrom = getAuthenticated();
        userFrom.getCardSet().stream()
                .filter(card1 -> card1.getCardId().equals(cardId))
                .findAny()
                .orElseThrow(() -> new IllegalCardIdPassed(cardId + " is not your card"));
        cardsService.deposit(cardId, amount);
    }
}
