package com.example.bank.controller;

import com.example.bank.dto.CardDTO;
import com.example.bank.dto.UserDTO;
import com.example.bank.dto.request.DepositCardDTO;
import com.example.bank.entity.User;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.exception.IllegalCardIdPassed;
import com.example.bank.service.CardsService;
import com.example.bank.service.UsersService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardsController {

    private final CardsService cardsService;

    public CardsController(@Qualifier("cardsServiceImpl") CardsService cardsService) {
        this.cardsService = cardsService;
    }

    @GetMapping
    public List<CardDTO> getAllCards(@AuthenticationPrincipal UserDTO user) {
        return cardsService.getAllUserCard(user.getId());
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardDTO createNewCard(@AuthenticationPrincipal UserDTO user) {
        return cardsService.createCard(user.getId());
    }

    @GetMapping("/{cardId}")
    public CardDTO getOneCards(@AuthenticationPrincipal final UserDTO user,
                               @PathVariable Integer cardId) {
        return cardsService.checkIsUsersCard(user.getId(), cardId).orElseThrow(IllegalCardIdPassed::new);
    }

    @PostMapping("/{cardId}")
    public CardDTO depositCard(@AuthenticationPrincipal final UserDTO user,
                            @PathVariable Integer cardId,
                            @RequestBody DepositCardDTO depositCardDTO){
        if (depositCardDTO.getAmount() <= 0) {
            throw new IllegalArgumentsPassed("amount less than or equal zero");
        }
        CardDTO card = cardsService.checkIsUsersCard(user.getId(), cardId).orElseThrow(IllegalCardIdPassed::new);
        return cardsService.deposit(card.getCardId(), depositCardDTO.getAmount());
    }

    @DeleteMapping("/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOneCards(@AuthenticationPrincipal final UserDTO user,
                               @PathVariable Integer cardId) {
        CardDTO card = cardsService.checkIsUsersCard(user.getId(), cardId).orElseThrow(IllegalCardIdPassed::new);
        cardsService.deleteCardById(card.getCardId());
    }
}
