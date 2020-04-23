package com.example.bank.dao;

import com.example.bank.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Transfer findTransferByToken(String token);

    List<Transfer> findAllByExecutedTrueAndCardFrom_CardIdAndAmountBetweenAndTimeExecutedBetween(
            Long cardFromId, Long from, Long to, Date timeFrom, Date timeTo);

    List<Transfer> findAllByExecutedTrueAndCardFrom_CardIdAndCardTo_CardIdAndAmountBetweenAndTimeExecutedBetween(
            Long cardFromId, Long cardToId, Long amountFrom, Long amountTo,
            Date timeFrom, Date timeTo);
}
