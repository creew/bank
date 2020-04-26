package com.example.bank.service;

import com.example.bank.dto.response.CardDTO;
import com.example.bank.dto.request.CompleteTransferDTO;
import com.example.bank.dto.response.VerifyTransferDTO;
import com.example.bank.entity.Transfer;

import java.util.Optional;

public interface TransfersService {

    Optional<Transfer> findTransferByToken(String token);

    void setTransferComplete(Transfer transfer);

    VerifyTransferDTO createVerifyRequest(int userFromId, int cardIdFrom, int cardIdTo, long amount);

    CardDTO completeTransfer(int userFromId, CompleteTransferDTO completeTransferDto);
}
