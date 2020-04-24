package com.example.bank.service.impl;

import com.example.bank.dao.TransferRepository;
import com.example.bank.dto.response.TransferInfoDTO;
import com.example.bank.entity.Card;
import com.example.bank.entity.Transfer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransfersServiceImpl implements com.example.bank.service.TransfersService {

    private TransferRepository transferRepository;

    public TransfersServiceImpl(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    @Override
    @Transactional
    public Transfer createNewTransfer(Card userFrom, Card userTo, long amount) {
        Transfer transfer = new Transfer(userFrom, userTo, amount);
        transferRepository.saveAndFlush(transfer);
        return transfer;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transfer> findTransferByToken(String token) {
        Transfer transfer = transferRepository.findTransferByToken(token);
        if (transfer != null && !transfer.isExecuted() && !transfer.hasExpired()) {
            return Optional.of(transfer);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void setTransferComplete(Transfer transfer) {
        transfer.setExecuted(true);
        transfer.setTimeExecuted(new Date());
        transferRepository.saveAndFlush(transfer);
    }


    @Override
    @Transactional(readOnly = true)
    public List<TransferInfoDTO> getTransfersOfUser(Long userIdFrom, Long amountFrom, Long amountTo,
                                                    Date dateFrom, Date dateTo) {
        return transferRepository.findAllByExecutedTrueAndCardFrom_CardIdAndAmountBetweenAndTimeExecutedBetween(
                userIdFrom, amountFrom, amountTo, dateFrom, dateTo).stream()
                .map(TransferInfoDTO::fromTransfer)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransferInfoDTO> getTransfersOfUserToUser(Long cardIdFrom, Long cardIdTo, Long amountFrom,
                                                          Long amountTo, Date dateFrom, Date dateTo) {
        return transferRepository.findAllByExecutedTrueAndCardFrom_CardIdAndCardTo_CardIdAndAmountBetweenAndTimeExecutedBetween(
                cardIdFrom, cardIdTo, amountFrom, amountTo, dateFrom, dateTo).stream()
                .map(TransferInfoDTO::fromTransfer)
                .collect(Collectors.toList());
    }

}
