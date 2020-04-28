package com.example.bank.dao;

import com.example.bank.entity.Transaction;

import java.util.Date;
import java.util.List;

public interface TransactionRepositoryCustom {

    List<Transaction> fetchAllTransferByUser(
            Long userId,
            Long cardIdTo,
            Long amountFrom,
            Long amountTo,
            Date timeFrom,
            Date timeTo
    );

    List<Transaction> fetchAllTransferByCard(
            Long cardId,
            Long cardIdTo,
            Long amountFrom,
            Long amountTo,
            Date timeFrom,
            Date timeTo
    );

}
