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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardsService {

    private final ModelMapper modelMapper;

    private final CardRepository cardRepository;

    private final VerificationTokenRepository verificationTokenRepository;

    public CardsService(ModelMapper modelMapper,
                        CardRepository cardRepository,
                        VerificationTokenRepository verificationTokenRepository) {
        this.modelMapper = modelMapper;
        this.cardRepository = cardRepository;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public CardDto mapCardToCardDto(Card card) {
        return modelMapper.map(card, CardDto.class);
    }

    public List<CardDto> mapCardsListToDto(Collection<Card> cards) {
        return cards.stream()
                .map(this::mapCardToCardDto)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public Card deposit(Long id, Long amount) {
        Card card = cardRepository.getOne(id);
        card.setAmount(card.getAmount() + amount);
        return cardRepository.saveAndFlush(card);
    }

    @Transactional
    public Card createCard() {
        Card card = new Card();
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
}
