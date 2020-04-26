package com.example.bank.controller;

import com.example.bank.dto.UserDTO;
import com.example.bank.dto.response.TransactionDTO;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.service.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionsController {

    @Autowired
    private TransactionsService transactionsService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionDTO> getTransfersInfo(@AuthenticationPrincipal UserDTO user,
                                                 @RequestParam(value = "from_date", required = false) Long fromDateLong,
                                                 @RequestParam(value = "to_date", required = false) Long toDateLong,
                                                 @RequestParam(value = "from_amount", required = false) Long fromAmount,
                                                 @RequestParam(value = "to_amount", required = false) Long toAmount,
                                                 @RequestParam(value = "to_user", required = false) Long userId) {
        Date curDate = new Date();
        Date fromDate = null;
        Date toDate = null;
        if (fromDateLong != null) {
            if (fromDateLong > curDate.getTime()) {
                throw new IllegalArgumentsPassed("Wrong from_date");
            }
            fromDate = new Date(fromDateLong);
        }
        if (toDateLong != null) {
            if (toDateLong > curDate.getTime()) {
                throw new IllegalArgumentsPassed("Wrong to_date");
            }
            toDate = new Date(toDateLong);
        }
        return transactionsService.getTransactionsOfUserToUser(user.getId(), userId, fromAmount,
                toAmount,fromDate, toDate);
    }
}
