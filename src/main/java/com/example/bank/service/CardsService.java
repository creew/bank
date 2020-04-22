package com.example.bank.service;

import com.example.bank.dao.CardRepository;
import com.example.bank.dao.VerificationTokenRepository;
import com.example.bank.dto.CardDTO;
import com.example.bank.dto.CompleteTransferDTO;
import com.example.bank.dto.VerifyTransferDTO;
import com.example.bank.entity.Card;
import com.example.bank.entity.User;
import com.example.bank.entity.VerificationToken;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.exception.IllegalCardIdPassed;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CardsService {

    private final CardRepository cardRepository;

    private final VerificationTokenRepository verificationTokenRepository;

    public CardsService(CardRepository cardRepository,
                        VerificationTokenRepository verificationTokenRepository) {
        this.cardRepository = cardRepository;
        this.verificationTokenRepository = verificationTokenRepository;
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
        verificationTokenRepository.deletePreviousTokens(cardIdFrom);
        VerificationToken verificationToken = new VerificationToken(userCard);
        verificationToken.setAmount(amount);
        verificationToken.setCardTo(cardTo);
        userCard.setVerificationToken(verificationToken);
        cardRepository.saveAndFlush(userCard);
        return new VerifyTransferDTO(cardTo.getUser().getPrincipal(), amount, verificationToken.getToken());
    }

    @Transactional
    public CardDTO completeTransfer(long userFromId, CompleteTransferDTO completeTransferDto) {
        String token = completeTransferDto.getToken();
        VerificationToken verificationToken = verificationTokenRepository
                .findVerificationTokenByToken(token);
        if (verificationToken == null) {
            throw new IllegalArgumentsPassed("Token not found");
        }
        if (!verificationToken.isActive() || verificationToken.hasExpired())
            throw new IllegalArgumentsPassed("Token expired");
        Card cardFrom = verificationToken.getCardFrom();
        Card cardTo = verificationToken.getCardTo();
        if (!cardFrom.getUser().getUserId().equals(userFromId))
            throw new IllegalArgumentsPassed("Card is not your");
        verificationToken.setActive(false);
        verificationTokenRepository.saveAndFlush(verificationToken);
        Long amount = verificationToken.getAmount();
        cardFrom.setAmount(cardFrom.getAmount() - amount);
        cardTo.setAmount(cardTo.getAmount() + amount);
        Card updatedFrom = cardRepository.save(cardFrom);
        if (updatedFrom.getAmount() < 0)
            throw new IllegalArgumentsPassed("Not enough money");
        cardRepository.saveAndFlush(cardTo);
        return CardDTO.fromCard(updatedFrom);
    }

    @Transactional(readOnly = true)
    public Optional<Card> checkIsUsersCard(User user, Long cardId) {
        Card card = cardRepository.getOne(cardId);
        if (card.getUser().equals(user))
            return Optional.of(card);
        return Optional.empty();
    }


    public void deleteCardById(Long cardId) {
        cardRepository.deleteById(cardId);
    }

    public void deleteCard(Card card) {
        cardRepository.deleteById(card.getCardId());
    }
}
