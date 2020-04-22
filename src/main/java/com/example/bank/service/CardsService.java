package com.example.bank.service;

import com.example.bank.dao.CardRepository;
import com.example.bank.dao.VerificationTokenRepository;
import com.example.bank.dto.CardDto;
import com.example.bank.dto.CompleteTransferDto;
import com.example.bank.dto.VerifyTransferDto;
import com.example.bank.entity.Card;
import com.example.bank.entity.User;
import com.example.bank.entity.VerificationToken;
import com.example.bank.exception.IllegalArgumentsPassed;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Card deposit(Long id, Long amount) {
        Card card = cardRepository.getOne(id);
        card.setAmount(card.getAmount() + amount);
        return cardRepository.saveAndFlush(card);
    }

    @Transactional
    public Card createCard(User user) {
        Card card = new Card(user, 0L);
        return cardRepository.saveAndFlush(card);
    }

    @Transactional
    public VerifyTransferDto createVerifyRequest(User userFrom, long cardIdFrom, long cardIdTo, long amount) {
        Card userCard = cardRepository.getOne(cardIdFrom);
        if (!userCard.getUser().equals(userFrom))
            throw new IllegalArgumentsPassed("Card id: " + cardIdFrom + " is not your");
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
        return new VerifyTransferDto(cardTo.getUser().getPrincipal(), amount, verificationToken.getToken());
    }

    @Transactional
    public CardDto completeTransfer(User userFrom, CompleteTransferDto completeTransferDto) {
        String token = completeTransferDto.getToken();
        VerificationToken verificationToken = verificationTokenRepository
                .findVerificationTokenByToken(token);
        if (token == null) {
            throw new IllegalArgumentsPassed("Token not found");
        }
        if (verificationToken.hasExpired())
            throw new IllegalArgumentsPassed("Token expired");
        Card cardFrom = verificationToken.getCardFrom();
        Card cardTo = verificationToken.getCardTo();
        if (!cardFrom.getUser().equals(userFrom))
            throw new IllegalArgumentsPassed("Card is not your");
        Long amount = verificationToken.getAmount();
        cardFrom.setAmount(cardFrom.getAmount() - amount);
        cardTo.setAmount(cardTo.getAmount() + amount);
        Card updatedFrom = cardRepository.save(cardFrom);
        if (updatedFrom.getAmount() < 0)
            throw new IllegalArgumentsPassed("Not enough money");
        cardRepository.save(cardTo);
        cardRepository.flush();
        return CardDto.fromCard(updatedFrom);
    }

    @Transactional(readOnly = true)
    public boolean checkIsUsersCard(User user, Long cardId) {
        Card card = cardRepository.getOne(cardId);
        return card.getUser().equals(user);
    }


    public void deleteCardById(Long cardId) {
        cardRepository.deleteById(cardId);
    }
}
