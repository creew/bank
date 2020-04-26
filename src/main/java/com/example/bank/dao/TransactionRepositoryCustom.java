package com.example.bank.dao;

import com.example.bank.entity.Transaction;

import java.util.Date;
import java.util.List;

public interface TransactionRepositoryCustom {
    List<Transaction> fetchAllTransferByUserToUser(
            Long userId,
            Long userToId,
            Long amountFrom,
            Long amountTo,
            Date timeFrom,
            Date timeTo
    );
}
