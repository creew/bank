package com.example.bank.service.impl;

import com.example.bank.dao.CardRepository;
import com.example.bank.dto.response.CardDTO;
import com.example.bank.dto.request.CompleteTransferDTO;
import com.example.bank.dto.response.VerifyTransferDTO;
import com.example.bank.entity.Card;
import com.example.bank.entity.Transfer;
import com.example.bank.entity.User;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.exception.IllegalCardIdPassed;
import com.example.bank.service.CardsService;
import com.example.bank.service.TransactionsService;
import com.example.bank.service.TransfersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardsServiceImpl implements CardsService {

    private final CardRepository cardRepository;

    private final TransfersService transfersService;

    private final TransactionsService transactionsService;

    private static final Logger logger = LoggerFactory.getLogger(CardsServiceImpl.class);

    public CardsServiceImpl(CardRepository cardRepository,
                            TransfersService transfersService, TransactionsService transactionsService) {
        this.cardRepository = cardRepository;
        this.transfersService = transfersService;
        this.transactionsService = transactionsService;
    }

    @Override
    @Transactional
    public CardDTO deposit(long cardId, Long amount) {
        Card card = cardRepository.getOne(cardId);
        card.setAmount(card.getAmount() + amount);
        transactionsService.saveTransaction(null, card, amount, new Date());
        return CardDTO.fromCard(cardRepository.saveAndFlush(card));
    }

    @Override
    @Transactional
    public CardDTO createCard(User user) {
        Card card = new Card(user, 0L);
        return CardDTO.fromCard(cardRepository.saveAndFlush(card));
    }

    @Override
    @Transactional
    public VerifyTransferDTO createVerifyRequest(long userFromId, long cardIdFrom, long cardIdTo, long amount) {
        Card userCard = cardRepository.getOne(cardIdFrom);
        if (!userCard.getUser().getUserId().equals(userFromId))
            throw new IllegalCardIdPassed("Card id: " + cardIdFrom + " is not your");
        if (cardIdFrom == cardIdTo)
            throw new IllegalArgumentsPassed("Cards from and to are identical");
        Card cardTo = cardRepository.getOne(cardIdTo);
        if (userCard.getAmount() < amount)
            throw new IllegalArgumentsPassed("No funds available");
        return  transfersService.createNewTransfer(userCard, cardTo, amount);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CardDTO completeTransfer(long userFromId, CompleteTransferDTO completeTransferDto) {
        String token = completeTransferDto.getToken();
        Transfer transfer = transfersService.findTransferByToken(token).orElseThrow(
                () -> new IllegalArgumentsPassed("Token not exist or expired"));
        Card cardFrom = transfer.getCardFrom();
        Card cardTo = transfer.getCardTo();
        if (!cardFrom.getUser().getUserId().equals(userFromId))
            throw new IllegalArgumentsPassed("Card is not your");
        Long amount = transfer.getAmount();
        logger.info("Complete transfer: from: {}, to: {}, amount: {}", CardDTO.fromCard(cardFrom),
                CardDTO.fromCard(cardTo), amount);
        cardFrom.setAmount(cardFrom.getAmount() - amount);
        cardTo.setAmount(cardTo.getAmount() + amount);
        Card updatedFrom = cardRepository.save(cardFrom);
        if (updatedFrom.getAmount() < 0)
            throw new IllegalArgumentsPassed("Not enough money");
        logger.info("Result complete transfer: {}", CardDTO.fromCard(updatedFrom));
        cardRepository.saveAndFlush(cardTo);
        transfersService.setTransferComplete(transfer);
        transactionsService.saveTransaction(cardFrom, cardTo, amount, new Date());
        return CardDTO.fromCard(updatedFrom);
    }

    @Override
    @Transactional(readOnly = true)
    public CardDTO checkIsUsersCard(long userId, long cardId) {
        Card card = cardRepository.getOne(cardId);
        if (card.getUser().getUserId().equals(userId))
            return CardDTO.fromCard(card);
        throw new IllegalCardIdPassed();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardDTO> getAllUserCard(long userId) {
        return cardRepository.findAllByUser_UserId(userId).stream()
                .map(CardDTO::fromCard)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCardById(long cardId) {
        cardRepository.deleteById(cardId);
    }

}
