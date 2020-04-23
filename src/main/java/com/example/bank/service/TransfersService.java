package com.example.bank.service;

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

    @Transactional(readOnly = true)
    public Optional<Transfer> findTransferByToken(String token) {
        Transfer transfer = transferRepository.findTransferByToken(token);
        if (transfer != null && !transfer.isExecuted() && !transfer.hasExpired()) {
            return Optional.of(transfer);
        }
        return Optional.empty();
    }

    @Transactional
    public void setTransferComplete(Transfer transfer) {
        transfer.setExecuted(true);
        transfer.setTimeExecuted(new Date());
        transferRepository.saveAndFlush(transfer);
    }


    @Transactional(readOnly = true)
    public List<TransferInfoDTO> getTransfersOfUser(Long userIdFrom, Long amountFrom, Long amountTo,
                                                    Date dateFrom, Date dateTo) {
        return transferRepository.findAllByExecutedTrueAndCardFrom_CardIdAndAmountBetweenAndTimeExecutedBetween(
                userIdFrom, amountFrom, amountTo, dateFrom, dateTo).stream()
                .map(TransferInfoDTO::fromTransfer)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransferInfoDTO> getTransfersOfUserToUser(Long cardIdFrom, Long cardIdTo, Long amountFrom,
                                             Long amountTo, Date dateFrom, Date dateTo) {
        return transferRepository.findAllByExecutedTrueAndCardFrom_CardIdAndCardTo_CardIdAndAmountBetweenAndTimeExecutedBetween(
                cardIdFrom, cardIdTo, amountFrom, amountTo, dateFrom, dateTo).stream()
                .map(TransferInfoDTO::fromTransfer)
                .collect(Collectors.toList());
    }

}
