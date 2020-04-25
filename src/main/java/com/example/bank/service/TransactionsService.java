package com.example.bank.service;

import com.example.bank.dto.response.TransactionDTO;
import com.example.bank.entity.Card;

import java.util.Date;
import java.util.List;

public interface TransactionsService {

    List<TransactionDTO> getTransactionsOfUser(int userIdFrom, Long amountFrom, Long amountTo,
                                               Date dateFrom, Date dateTo);

    List<TransactionDTO> getTransactionsOfUserToUser(int cardIdFrom, Long cardIdTo, Long amountFrom,
                                                     Long amountTo, Date dateFrom, Date dateTo);

    void saveTransaction(Card cardFrom, Card cardTo, Long amount, Date date);
}
