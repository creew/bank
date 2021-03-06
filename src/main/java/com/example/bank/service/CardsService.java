package com.example.bank.service;

import com.example.bank.dto.response.CardDTO;
import com.example.bank.dto.request.CompleteTransferDTO;
import com.example.bank.dto.response.VerifyTransferDTO;
import com.example.bank.entity.User;

import java.util.List;

public interface CardsService {

    CardDTO deposit(long cardId, Long amount);

    CardDTO createCard(User user);

    VerifyTransferDTO createVerifyRequest(long userFromId, long cardIdFrom, long cardIdTo, long amount);

    CardDTO completeTransfer(long userFromId, CompleteTransferDTO completeTransferDto);

    CardDTO checkIsUsersCard(long userId, long cardId);

    List<CardDTO> getAllUserCard(long userId);

    void deleteCardById(long cardId);

}
