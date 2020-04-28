package com.example.bank.controller;

import com.example.bank.dto.UserDTO;
import com.example.bank.dto.response.TransactionDTO;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.service.CardsService;
import com.example.bank.service.TransactionsService;
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

    private void checkDates(Long fromDateLong, Long toDateLong) {
        Date curDate = new Date();
        if (fromDateLong != null && fromDateLong > curDate.getTime()) {
            throw new IllegalArgumentsPassed("Wrong from_date");
        }
        if (toDateLong != null && toDateLong > curDate.getTime()) {
            throw new IllegalArgumentsPassed("Wrong to_date");
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionDTO> getTransfersInfo(@AuthenticationPrincipal final UserDTO user,
                                                 @RequestParam(value = "from_date", required = false) Long fromDateLong,
                                                 @RequestParam(value = "to_date", required = false) Long toDateLong,
                                                 @RequestParam(value = "from_amount", required = false) Long fromAmount,
                                                 @RequestParam(value = "to_amount", required = false) Long toAmount,
                                                 @RequestParam(value = "to_card", required = false) Long toCard) {
        checkDates(fromDateLong, toDateLong);
        return transactionsService.getTransactionsOfUser(user.getId(), toCard, fromAmount,
                toAmount, fromDateLong, toDateLong);
    }

    @GetMapping("/{cardId}")
    public List<TransactionDTO> getOneCardTransactions(@AuthenticationPrincipal final UserDTO user,
                                                       @PathVariable("cardId") Long cardId,
                                                       @RequestParam(value = "from_date", required = false) Long fromDateLong,
                                                       @RequestParam(value = "to_date", required = false) Long toDateLong,
                                                       @RequestParam(value = "from_amount", required = false) Long fromAmount,
                                                       @RequestParam(value = "to_amount", required = false) Long toAmount,
                                                       @RequestParam(value = "to_card", required = false) Long toCard) {
        cardsService.checkIsUsersCard(user.getId(), cardId);
        checkDates(fromDateLong, toDateLong);
        return transactionsService.getTransactionsOfCard(cardId, toCard, fromAmount,
                toAmount, fromDateLong, toDateLong);
    }
}
