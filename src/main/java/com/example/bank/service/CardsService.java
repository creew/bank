package com.example.bank.service;

import com.example.bank.dao.CardRepository;
import com.example.bank.dto.CardDTO;
import com.example.bank.dto.request.CompleteTransferDTO;
import com.example.bank.dto.response.VerifyTransferDTO;
import com.example.bank.entity.Card;
import com.example.bank.entity.Transfer;
import com.example.bank.entity.User;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.exception.IllegalCardIdPassed;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CardsService {

    private final CardRepository cardRepository;

    private final TransfersService transfersService;

    public CardsService(CardRepository cardRepository,
                        TransfersService transfersService) {
        this.cardRepository = cardRepository;
        this.transfersService = transfersService;
    }

    @Transactional
    public Card deposit(Card card, Long amount) {
        card.setAmount(card.getAmount() + amount);
        return cardRepository.saveAndFlush(card);
    }

    @Transactional
    public Card createCard(User user) {
        Card card = new Card(user, 0L);
        return cardRepository.saveAndFlush(card);
    }

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
        Transfer transfer = transfersService.createNewTransfer(userCard, cardTo, amount);
        return new VerifyTransferDTO(cardTo.getUser().getPrincipal(), amount, transfer.getToken());
    }

    @Transactional
    public CardDTO completeTransfer(long userFromId, CompleteTransferDTO completeTransferDto) {
        String token = completeTransferDto.getToken();
        Transfer transfer = transfersService.findTransferByToken(token).orElseThrow(
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
        transfersService.setTransferComplete(transfer);
        return CardDTO.fromCard(updatedFrom);
    }

    @Transactional(readOnly = true)
    public Optional<Card> checkIsUsersCard(User user, Long cardId) {
        Card card = cardRepository.getOne(cardId);
        if (card.getUser().equals(user))
            return Optional.of(card);
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public List<CardDTO> getAllUserCard(long userId) {
        return cardRepository.findAllByUser_UserId(userId).stream()
                .map(CardDTO::fromCard)
                .collect(Collectors.toList());
    }

    public void deleteCardById(Long cardId) {
        cardRepository.deleteById(cardId);
    }

    public void deleteCard(Card card) {
        cardRepository.deleteById(card.getCardId());
    }
}
