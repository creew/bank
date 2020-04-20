package com.example.bank.controller;

import com.example.bank.dto.AuthenticatedUserTokenDto;
import com.example.bank.dto.CredentialsDto;
import com.example.bank.dto.UserDto;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signin")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AuthenticatedUserTokenDto login(@RequestBody CredentialsDto credentialsDto) {
        return userService.loginUser(credentialsDto);
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthenticatedUserTokenDto registerUser(@RequestBody UserDto userDto) {
        if (!userDto.getPassword().equals(userDto.getPasswordConfirm())){
            throw new IllegalArgumentsPassed("Пароли не совпадают");
        }
        return userService.createUser(userDto);
    }

}
