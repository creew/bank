package com.example.bank.controller;

import com.example.bank.dto.CardDto;
import com.example.bank.dto.CompleteTransferDto;
import com.example.bank.dto.VerifyTransferDto;
import com.example.bank.entity.User;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.exception.IllegalCardIdPassed;
import com.example.bank.service.CardsService;
import com.example.bank.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Controller {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CardsService cardsService;

    private final UserService userService;

    public Controller(CardsService cardsService, UserService userService) {
        this.cardsService = cardsService;
        this.userService = userService;
    }

    @GetMapping("/cards")
    public List<CardDto> getAllCards(@AuthenticationPrincipal final User user) {
        User fromBase = userService.getUserById(user.getUserId());
        List<CardDto> tasksList;
        tasksList = cardsService.mapCardsListToDto(fromBase.getCardSet());
        return tasksList;
    }

    @GetMapping("/transfer/{cardIdFrom}")
    @ResponseStatus(HttpStatus.OK)
    public VerifyTransferDto transferRequest(@AuthenticationPrincipal User user,
                                              @PathVariable Long cardIdFrom,
                                              @RequestParam("card_id_to") Long cardIdTo,
                                              @RequestParam("amount") Long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentsPassed("Amount less than or equal zero");
        }
        return cardsService.createVerifyRequest(user, cardIdFrom, cardIdTo, amount);
    }

    @PutMapping("/transfer")
    @ResponseStatus(HttpStatus.OK)
    public CardDto transferComplete(@AuthenticationPrincipal User user,
                                              @RequestBody CompleteTransferDto completeTransfer) {
        return cardsService.completeTransfer(user, completeTransfer );
    }

    @PutMapping("/deposit/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deposit(@AuthenticationPrincipal User user,
                        @PathVariable Long cardId,
                        @RequestParam("amount") Long amount) {
        user.getCardSet().stream()
                .filter(card1 -> card1.getCardId().equals(cardId))
                .findAny()
                .orElseThrow(() -> new IllegalCardIdPassed(cardId + " is not your card"));
        cardsService.deposit(cardId, amount);
    }
}
