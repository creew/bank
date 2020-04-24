package com.example.bank.dao;

import com.example.bank.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Transfer findTransferByToken(String token);

    @Query("SELECT t FROM Transfer t WHERE t.cardFrom.cardId = ?1 and" +
            " t.amount BETWEEN ?2 AND ?3 AND t.timeCreated BETWEEN ?4 AND ?5")
    List<Transfer> fetchAllTransferByUser(
            Long cardFromId, Long from, Long to, Date timeFrom, Date timeTo);

    @Query("SELECT t FROM Transfer t WHERE t.cardFrom.cardId = ?1 and t.cardTo.cardId = ?2 and " +
            " t.amount BETWEEN ?3 AND ?4 AND t.timeCreated BETWEEN ?5 AND ?6")
    List<Transfer> fetchAllTransferByUserToUser(
            Long cardFromId, Long cardToId, Long amountFrom, Long amountTo,
            Date timeFrom, Date timeTo);
}
