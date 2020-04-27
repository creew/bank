package com.example.bank.controller;

import com.example.bank.dto.UserDTO;
import com.example.bank.dto.response.TransactionDTO;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.exception.IllegalCardIdPassed;
import com.example.bank.service.CardsService;
import com.example.bank.service.TransactionsService;
import com.example.bank.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionsController {

    private final TransactionsService transactionsService;

    private final CardsService cardsService;

    public TransactionsController(TransactionsService transactionsService,
                                  CardsService cardsService) {
        this.transactionsService = transactionsService;
        this.cardsService = cardsService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionDTO> getTransfersInfo(@AuthenticationPrincipal UserDTO user,
                                                 @RequestParam(value = "from_date", required = false) Long fromDateLong,
                                                 @RequestParam(value = "to_date", required = false) Long toDateLong,
                                                 @RequestParam(value = "from_amount", required = false) Long fromAmount,
                                                 @RequestParam(value = "to_amount", required = false) Long toAmount,
                                                 @RequestParam(value = "to_card", required = false) Long toCard) {
        Date curDate = new Date();
        if (fromDateLong != null && fromDateLong > curDate.getTime()) {
            throw new IllegalArgumentsPassed("Wrong from_date");
        }
        if (toDateLong != null && toDateLong > curDate.getTime()) {
            throw new IllegalArgumentsPassed("Wrong to_date");
        }
        return transactionsService.getTransactionsOfUser(user.getId(), toCard, fromAmount,
                toAmount, fromDateLong, toDateLong);
    }

    @GetMapping("/{cardId}")
    public List<TransactionDTO> getOneCardTransactions(@AuthenticationPrincipal UserDTO user,
                                                       @PathVariable("cardId") Long cardId,
                                                       @RequestParam(value = "from_date", required = false) Long fromDateLong,
                                                       @RequestParam(value = "to_date", required = false) Long toDateLong,
                                                       @RequestParam(value = "from_amount", required = false) Long fromAmount,
                                                       @RequestParam(value = "to_amount", required = false) Long toAmount,
                                                       @RequestParam(value = "to_card", required = false) Long toCard) {
        cardsService.checkIsUsersCard(user.getId(), cardId)
                .orElseThrow(IllegalCardIdPassed::new);
        Date curDate = new Date();
        if (fromDateLong != null && fromDateLong > curDate.getTime()) {
            throw new IllegalArgumentsPassed("Wrong from_date");
        }
        if (toDateLong != null && toDateLong > curDate.getTime()) {
            throw new IllegalArgumentsPassed("Wrong to_date");
        }
        return transactionsService.getTransactionsOfCard(cardId, toCard, fromAmount,
                toAmount, fromDateLong, toDateLong);
    }
}
