package com.example.bank.dao;

import com.example.bank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT u " +
            "FROM Transaction u " +
            "WHERE " +
            "(u.cardFrom.user.userId = ?1 OR u.cardTo.user.userId = ?1) AND " +
            "u.amount BETWEEN ?2 AND ?3 AND " +
            "u.timeExecuted BETWEEN ?4 AND ?5")
    List<Transaction> fetchAllTransferByUser(
            Long userId,
            Long from,
            Long to,
            Date timeFrom,
            Date timeTo
    );

    @Query("SELECT u FROM Transaction u WHERE u.cardFrom.cardId = ?1 and u.cardTo.cardId = ?2 and " +
            " u.amount BETWEEN ?3 AND ?4 AND u.timeExecuted BETWEEN ?5 AND ?6")
    List<Transaction> fetchAllTransferByUserToUser(
            Long userId,
            Long userToId,
            Long amountFrom,
            Long amountTo,
            Date timeFrom,
            Date timeTo
    );

}