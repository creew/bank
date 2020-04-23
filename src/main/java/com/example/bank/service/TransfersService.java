package com.example.bank.service;

import com.example.bank.dao.TransferRepository;
import com.example.bank.entity.Card;
import com.example.bank.entity.Transfer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class TransfersService {

    private TransferRepository transferRepository;

    public TransfersService(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    @Transactional
    public Transfer createNewTransfer(Card userFrom, Card userTo, long amount) {
        Transfer transfer = new Transfer(userFrom, userTo, amount);
        transferRepository.saveAndFlush(transfer);
        return transfer;
    }

    @Transactional
    public Optional<Transfer> findTransferByToken(String token) {
        Transfer transfer = transferRepository.findTransferByToken(token);
        if (transfer != null && transfer.isActive() && !transfer.hasExpired()) {
            return Optional.of(transfer);
        }
        return Optional.empty();
    }

    @Transactional
    public void setTransferComplete(Transfer transfer) {
        transfer.setActive(false);
        transfer.setTimeExecuted(new Date());
        transferRepository.saveAndFlush(transfer);
    }


}
