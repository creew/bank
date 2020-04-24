package com.example.bank.controller;

import com.example.bank.dto.CardDTO;
import com.example.bank.dto.UserDTO;
import com.example.bank.dto.request.CompleteTransferDTO;
import com.example.bank.dto.request.RequestTransferDTO;
import com.example.bank.dto.response.TransferInfoDTO;
import com.example.bank.dto.response.VerifyTransferDTO;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.service.CardsService;
import com.example.bank.service.TransfersService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TransferInfoDTO> getTransfersInfo(@AuthenticationPrincipal UserDTO user,
                                                  @RequestParam(value = "from_date", required = false) Long fromDateLong,
                                                  @RequestParam(value = "to_date", required = false) Long toDateLong,
                                                  @RequestParam(value = "from_amount", required = false) Long fromAmount,
                                                  @RequestParam(value = "to_amount", required = false) Long toAmount,
                                                  @RequestParam(value = "to_user", required = false) Long userId) {
        Date curDate = new Date();
        Date fromDate;
        Date toDate;
        if (fromDateLong == null) {
            fromDate = new Date(0);
        } else {
            if (fromDateLong > curDate.getTime()) {
                throw new IllegalArgumentsPassed("Wrong from_date");
            }
            fromDate = new Date(fromDateLong);
        }
        if (toDateLong == null) {
            toDate = curDate;
        } else {
            if (toDateLong > curDate.getTime()) {
                throw new IllegalArgumentsPassed("Wrong to_date");
            }
            toDate = new Date(toDateLong);
        }
        if (fromAmount == null)
            fromAmount = 0L;
        if (toAmount == null)
            toAmount = Long.MAX_VALUE;
        if (userId == null) {
            return transfersService.getTransfersOfUser(user.getId(), fromAmount, toAmount, fromDate, toDate);
        } else {
            return transfersService.getTransfersOfUserToUser(user.getId(), userId, fromAmount, toAmount,
                    fromDate, toDate);
        }
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public CardDTO transferComplete(@AuthenticationPrincipal UserDTO user,
                                    @RequestBody @Valid CompleteTransferDTO completeTransfer) {
        return cardsService.completeTransfer(user.getId(), completeTransfer );
    }
}
