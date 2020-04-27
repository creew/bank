package com.example.bank.service.impl;

import com.example.bank.dao.TransferRepository;
import com.example.bank.dto.response.VerifyTransferDTO;
import com.example.bank.entity.Card;
import com.example.bank.entity.Transfer;
import com.example.bank.service.TransfersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class TransfersServiceImpl implements TransfersService {

    private final TransferRepository transferRepository;

    public TransfersServiceImpl(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    @Override
    @Transactional
    public VerifyTransferDTO createNewTransfer(Card userFrom, Card userTo, long amount) {
        Transfer transfer = new Transfer(userFrom, userTo, amount);
        return VerifyTransferDTO.fromTransfer(transferRepository.saveAndFlush(transfer));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transfer> findTransferByToken(String token) {
        Transfer transfer = transferRepository.findTransferByToken(UUID.fromString(token));
        if (transfer != null && !transfer.isExecuted() && !transfer.hasExpired()) {
            return Optional.of(transfer);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void setTransferComplete(Transfer transfer) {
        transfer.setExecuted(true);
        transferRepository.saveAndFlush(transfer);
    }

}
