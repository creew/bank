package com.example.bank.service;

import com.example.bank.dto.response.TransferInfoDTO;
import com.example.bank.dto.response.VerifyTransferDTO;
import com.example.bank.entity.Card;
import com.example.bank.entity.Transfer;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransfersService {

    VerifyTransferDTO createNewTransfer(Card userFrom, Card userTo, long amount);

    Optional<Transfer> findTransferByToken(String token);

    void setTransferComplete(Transfer transfer);

    List<TransferInfoDTO> getTransfersOfUser(Long userIdFrom, Long amountFrom, Long amountTo,
                                             Date dateFrom, Date dateTo);

    List<TransferInfoDTO> getTransfersOfUserToUser(Long cardIdFrom, Long cardIdTo, Long amountFrom,
                                                   Long amountTo, Date dateFrom, Date dateTo);
}
