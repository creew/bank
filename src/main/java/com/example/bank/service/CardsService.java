package com.example.bank.service;

import com.example.bank.dto.response.CardDTO;

import java.util.List;
import java.util.Optional;

public interface CardsService {

    CardDTO deposit(Integer cardId, Long amount);

    Optional<CardDTO> checkIsUsersCard(int userId, int cardId);

    List<CardDTO> getAllUserCard(int userId);

    void deleteCardById(int cardId);

    CardDTO createCard(int userId);

}
