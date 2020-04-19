package com.example.bank.service;

import com.example.bank.dto.CardDto;
import com.example.bank.entity.Card;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardsService {

    @Autowired
    private ModelMapper modelMapper;

    public List<CardDto> mapCardsListToDto(Collection<Card> cards) {
        return cards.stream()
                .map(card -> modelMapper.map(card, CardDto.class))
                .collect(Collectors.toList());
    }
}
