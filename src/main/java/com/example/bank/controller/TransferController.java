package com.example.bank.controller;

import com.example.bank.dto.CardDTO;
import com.example.bank.dto.UserDTO;
import com.example.bank.dto.request.CompleteTransferDTO;
import com.example.bank.dto.request.RequestTransferDTO;
import com.example.bank.dto.response.VerifyTransferDTO;
import com.example.bank.service.CardsService;
import com.example.bank.service.TransfersService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/transfer")
public class TransferController {

    private final CardsService cardsService;

    private final TransfersService transfersService;

    public TransferController(CardsService cardsService, TransfersService transfersService) {
        this.cardsService = cardsService;
        this.transfersService = transfersService;
    }

    @PutMapping("/{cardIdFrom}")
    @ResponseStatus(HttpStatus.OK)
    public VerifyTransferDTO transferRequest(@AuthenticationPrincipal UserDTO user,
                                             @PathVariable Long cardIdFrom,
                                             @RequestBody @Valid RequestTransferDTO requestTransferDTO) {
        return cardsService.createVerifyRequest(user.getId(), cardIdFrom, requestTransferDTO.getCardIdTo(),
                requestTransferDTO.getAmount());
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public CardDTO transferComplete(@AuthenticationPrincipal UserDTO user,
                                    @RequestBody @Valid CompleteTransferDTO completeTransfer) {
        return cardsService.completeTransfer(user.getId(), completeTransfer );
    }
}
