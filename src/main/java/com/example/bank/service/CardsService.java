package com.example.bank.service;

import com.example.bank.dto.CardDTO;
import com.example.bank.dto.request.CompleteTransferDTO;
import com.example.bank.dto.response.VerifyTransferDTO;
import com.example.bank.entity.User;

import java.util.List;
import java.util.Optional;

public interface CardsService {

    CardDTO deposit(Integer cardId, Long amount);

    VerifyTransferDTO createVerifyRequest(int userFromId, int cardIdFrom, int cardIdTo, long amount);

    CardDTO completeTransfer(int userFromId, CompleteTransferDTO completeTransferDto);

    Optional<CardDTO> checkIsUsersCard(int userId, int cardId);

    List<CardDTO> getAllUserCard(int userId);

    void deleteCardById(int cardId);

    CardDTO createCard(int userId);

}
