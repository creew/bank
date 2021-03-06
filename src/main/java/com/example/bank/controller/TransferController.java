package com.example.bank.controller;

import com.example.bank.dto.response.CardDTO;
import com.example.bank.dto.UserDTO;
import com.example.bank.dto.request.CompleteTransferDTO;
import com.example.bank.dto.request.RequestTransferDTO;
import com.example.bank.dto.response.VerifyTransferDTO;
import com.example.bank.service.CardsService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/transfer")
public class TransferController {

    private final CardsService cardsService;

    public TransferController(CardsService cardsService) {
        this.cardsService = cardsService;
    }

    @PutMapping("/{cardIdFrom}")
    @ResponseStatus(HttpStatus.OK)
    public VerifyTransferDTO transferRequest(@AuthenticationPrincipal final UserDTO user,
                                             @PathVariable Long cardIdFrom,
                                             @RequestBody @Valid RequestTransferDTO requestTransferDTO) {
        return cardsService.createVerifyRequest(user.getId(), cardIdFrom, requestTransferDTO.getCardIdTo(),
                requestTransferDTO.getAmount());
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public CardDTO transferComplete(@AuthenticationPrincipal final UserDTO user,
                                    @RequestBody @Valid CompleteTransferDTO completeTransfer) {
        return cardsService.completeTransfer(user.getId(), completeTransfer );
    }
}
