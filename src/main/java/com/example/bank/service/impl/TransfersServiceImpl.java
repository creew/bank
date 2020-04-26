package com.example.bank.service.impl;

import com.example.bank.dao.CardRepository;
import com.example.bank.dao.TransferRepository;
import com.example.bank.dto.response.CardDTO;
import com.example.bank.dto.request.CompleteTransferDTO;
import com.example.bank.dto.response.VerifyTransferDTO;
import com.example.bank.entity.Card;
import com.example.bank.entity.Transfer;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.exception.IllegalCardIdPassed;
import com.example.bank.service.TransactionsService;
import com.example.bank.service.TransfersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class TransfersServiceImpl implements TransfersService {

    private final TransferRepository transferRepository;

    private final CardRepository cardRepository;

    private final TransactionsService transactionsService;

    public TransfersServiceImpl(TransferRepository transferRepository, CardRepository cardRepository, TransactionsService transactionsService) {
        this.transferRepository = transferRepository;
        this.cardRepository = cardRepository;
        this.transactionsService = transactionsService;
    }

    @Transactional
    public VerifyTransferDTO createNewTransfer(Card userFrom, Card userTo, long amount) {
        Transfer transfer = new Transfer(userFrom, userTo, amount);
        return VerifyTransferDTO.fromTransfer(transferRepository.saveAndFlush(transfer));
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
        transferRepository.saveAndFlush(transfer);
    }

    @Override
    @Transactional
    public VerifyTransferDTO createVerifyRequest(int userFromId, int cardIdFrom, int cardIdTo, long amount) {
        Card userCard = cardRepository.getOne(cardIdFrom);
        if (!userCard.getUser().getUserId().equals(userFromId))
            throw new IllegalCardIdPassed("Card id: " + cardIdFrom + " is not your");
        if (cardIdFrom == cardIdTo)
            throw new IllegalArgumentsPassed("Cards from and to are identical");
        Card cardTo = cardRepository.getOne(cardIdTo);
        if (userCard.getAmount() < amount)
            throw new IllegalArgumentsPassed("No funds available");
        return createNewTransfer(userCard, cardTo, amount);
    }

    @Override
    @Transactional
    public CardDTO completeTransfer(int userFromId, CompleteTransferDTO completeTransferDto) {
        String token = completeTransferDto.getToken();
        Transfer transfer = findTransferByToken(token).orElseThrow(
                () -> new IllegalArgumentsPassed("Token not exist or expired"));
        Card cardFrom = transfer.getCardFrom();
        Card cardTo = transfer.getCardTo();
        if (!cardFrom.getUser().getUserId().equals(userFromId))
            throw new IllegalArgumentsPassed("Card is not your");
        Long amount = transfer.getAmount();
        cardFrom.setAmount(cardFrom.getAmount() - amount);
        cardTo.setAmount(cardTo.getAmount() + amount);
        Card updatedFrom = cardRepository.save(cardFrom);
        if (updatedFrom.getAmount() < 0)
            throw new IllegalArgumentsPassed("Not enough money");
        cardRepository.saveAndFlush(cardTo);
        setTransferComplete(transfer);
        transactionsService.saveTransaction(cardFrom, cardTo, amount, new Date());
        return CardDTO.fromCard(updatedFrom);
    }
}
