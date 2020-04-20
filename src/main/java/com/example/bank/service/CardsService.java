package com.example.bank.service;

import com.example.bank.dao.CardRepository;
import com.example.bank.dto.CardDto;
import com.example.bank.entity.Card;
import com.example.bank.exception.IllegalArgumentsPassed;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardsService {

    private final ModelMapper modelMapper;

    private final CardRepository cardRepository;

    public CardsService(ModelMapper modelMapper, CardRepository cardRepository) {
        this.modelMapper = modelMapper;
        this.cardRepository = cardRepository;
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
    public Card transferMoney(Long idFrom, Long idTo, Long amount) {
        Card from = cardRepository.getOne(idFrom);
        Card to = cardRepository.getOne(idTo);
        from.setAmount(from.getAmount() - amount);
        to.setAmount(to.getAmount() + amount);
        Card updatedFrom = cardRepository.save(from);
        if (updatedFrom.getAmount() < 0)
            throw new IllegalArgumentsPassed("Not enough money");
        cardRepository.save(to);
        cardRepository.flush();
        return updatedFrom;
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
}
