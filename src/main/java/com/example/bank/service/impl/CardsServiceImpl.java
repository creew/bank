package com.example.bank.service.impl;

import com.example.bank.dao.CardRepository;
import com.example.bank.dao.UserRepository;
import com.example.bank.dto.response.CardDTO;
import com.example.bank.entity.Card;
import com.example.bank.entity.User;
import com.example.bank.service.CardsService;
import com.example.bank.service.TransactionsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CardsServiceImpl implements CardsService {

    private final CardRepository cardRepository;

    private final UserRepository userRepository;

    private final TransactionsService transactionsService;

    public CardsServiceImpl(CardRepository cardRepository,
                            @Qualifier("transactionsServiceImpl") TransactionsService transactionsService,
                            UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.transactionsService = transactionsService;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CardDTO deposit(Integer cardId, Long amount) {
        Card card = cardRepository.getOne(cardId);
        card.setAmount(card.getAmount() + amount);
        transactionsService.saveTransaction(null, card, amount, new Date());
        return CardDTO.fromCard(cardRepository.saveAndFlush(card));
    }



    @Override
    @Transactional(readOnly = true)
    public Optional<CardDTO> checkIsUsersCard(int userId, int cardId) {
        Card card = cardRepository.getOne(cardId);
        if (card.getUser().getUserId().equals(userId))
            return Optional.of(CardDTO.fromCard(card));
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardDTO> getAllUserCard(int userId) {
        return cardRepository.findAllByUser_UserId(userId).stream()
                .map(CardDTO::fromCard)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCardById(int cardId) {
        cardRepository.deleteById(cardId);
    }

    @Override
    @Transactional
    public CardDTO createCard(int userId) {
        User user = userRepository.getOne(userId) ;
        Card card = new Card(user, 0L);
        return CardDTO.fromCard(cardRepository.saveAndFlush(card));
    }
}
