package com.example.bank.controller;

import com.example.bank.dto.CardDTO;
import com.example.bank.dto.CompleteTransferDTO;
import com.example.bank.dto.RequestTransferDTO;
import com.example.bank.dto.VerifyTransferDTO;
import com.example.bank.entity.User;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.service.CardsService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfer")
public class TransferController {

    private final CardsService cardsService;

    public TransferController(CardsService cardsService) {
        this.cardsService = cardsService;
    }


    @PutMapping("/{cardIdFrom}")
    @ResponseStatus(HttpStatus.OK)
    public VerifyTransferDTO transferRequest(@AuthenticationPrincipal User user,
                                             @PathVariable Long cardIdFrom,
                                             @RequestBody RequestTransferDTO requestTransferDTO) {
        if (requestTransferDTO.getAmount() <= 0) {
            throw new IllegalArgumentsPassed("Amount less than or equal zero");
        }
        return cardsService.createVerifyRequest(user, cardIdFrom, requestTransferDTO.getCardIdTo(),
                requestTransferDTO.getAmount());
    }

    @PutMapping("")
    @ResponseStatus(HttpStatus.OK)
    public CardDTO transferComplete(@AuthenticationPrincipal User user,
                                    @RequestBody CompleteTransferDTO completeTransfer) {
        return cardsService.completeTransfer(user, completeTransfer );
    }
}
