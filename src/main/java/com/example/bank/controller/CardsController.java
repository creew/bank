package com.example.bank.controller;

import com.example.bank.dto.CardDTO;
import com.example.bank.dto.DepositCardDTO;
import com.example.bank.dto.UserDTO;
import com.example.bank.entity.Card;
import com.example.bank.entity.User;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.exception.IllegalCardIdPassed;
import com.example.bank.service.CardsService;
import com.example.bank.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cards")
public class CardsController {

    private final CardsService cardsService;

    private final UsersService usersService;

    public CardsController(CardsService cardsService, UsersService usersService) {
        this.cardsService = cardsService;
        this.usersService = usersService;
    }

    @GetMapping("")
    public List<CardDTO> getAllCards(@AuthenticationPrincipal UserDTO user) {
        User fromBase = usersService.getUserById(user.getId());
        return fromBase.getCardSet().stream()
                .map(CardDTO::fromCard)
                .collect(Collectors.toList());
    }

    @PutMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public CardDTO createNewCard(@AuthenticationPrincipal UserDTO user) {
        User fromBase = usersService.getUserById(user.getId());
        Card card = cardsService.createCard(fromBase);
        return CardDTO.fromCard(card);
    }

    @GetMapping("/{cardId}")
    public CardDTO getOneCards(@AuthenticationPrincipal final UserDTO user,
                               @PathVariable Long cardId) {
        User fromBase = usersService.getUserById(user.getId());
        Card card = cardsService.checkIsUsersCard(fromBase, cardId).orElseThrow(IllegalCardIdPassed::new);
        return CardDTO.fromCard(card);
    }

    @PostMapping("/{cardId}")
    public CardDTO depositCard(@AuthenticationPrincipal final UserDTO user,
                            @PathVariable Long cardId,
                            @RequestBody DepositCardDTO depositCardDTO){
        if (depositCardDTO.getAmount() <= 0) {
            throw new IllegalArgumentsPassed("amount less than or equal zero");
        }
        User fromBase = usersService.getUserById(user.getId());
        Card card = cardsService.checkIsUsersCard(fromBase, cardId).orElseThrow(IllegalCardIdPassed::new);
        return CardDTO.fromCard(cardsService.deposit(card, depositCardDTO.getAmount()));
    }

    @DeleteMapping("/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOneCards(@AuthenticationPrincipal final UserDTO user,
                               @PathVariable Long cardId) {
        User fromBase = usersService.getUserById(user.getId());
        Card card = cardsService.checkIsUsersCard(fromBase, cardId).orElseThrow(IllegalCardIdPassed::new);
        cardsService.deleteCard(card);
    }
}
