package com.example.bank.service;

import com.example.bank.dto.response.TransactionDTO;
import com.example.bank.entity.Card;

import java.util.Date;
import java.util.List;

public interface TransactionsService {

    List<TransactionDTO> getTransactionsOfUser(Long userId, Long cardIdTo, Long amountFrom,
                                               Long amountTo, Long dateFrom, Long dateTo);

    List<TransactionDTO> getTransactionsOfCard(Long cardId, Long cardIdTo, Long amountFrom,
                                               Long amountTo, Long dateFrom, Long dateTo);

    void saveTransaction(Card cardFrom, Card cardTo, Long amount, Date date);
}
