package com.example.bank.controller;

import com.example.bank.dto.UserDTO;
import com.example.bank.service.UsersService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    private final UsersService usersService;

    public Controller(@Qualifier("usersServiceJooq") UsersService usersService) {
        this.usersService = usersService;
    }

    @DeleteMapping("/users")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@AuthenticationPrincipal UserDTO user) {
        usersService.deleteUserById(user.getId());
    }
}
