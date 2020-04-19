package com.example.bank.service;

import com.example.bank.dao.CardRepository;
import com.example.bank.dto.CardDto;
import com.example.bank.entity.Card;
import com.example.bank.exception.IllegalArgumentsPassed;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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

    public List<CardDto> mapCardsListToDto(Collection<Card> cards) {
        return cards.stream()
                .map(card -> modelMapper.map(card, CardDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void transferMoney(Long idFrom, Long idTo, Long amount) {
        Card from = cardRepository.getOne(idFrom);
        Card to = cardRepository.getOne(idTo);
        Long amountFrom = from.getAmount();
        if (amountFrom < amount)
            throw new IllegalArgumentsPassed("No money available");
        from.setAmount(amountFrom - amount);
        to.setAmount(to.getAmount() + amount);
        cardRepository.saveAll(Arrays.asList(from, to));
        cardRepository.flush();
    }

    @Transactional(rollbackFor = Exception.class)
    public Card deposit(Long id, Long amount) {
        Card card = cardRepository.getOne(id);
        card.setAmount(card.getAmount() + amount);
        return cardRepository.saveAndFlush(card);
    }
}
