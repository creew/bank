package com.example.bank.controller;

import com.example.bank.service.UserAuthenticationService;
import com.example.bank.dto.CredentialsDto;
import com.example.bank.dto.UserDto;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.exception.WrongPasswordException;
import com.example.bank.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RequestMapping("/auth")
@RestController
public class AuthController {

    private UserService userService;

    UserAuthenticationService authenticationService;

    public AuthController(UserService userService, UserAuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signin")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, String> login(@RequestBody CredentialsDto credentialsDto) {
        String token = authenticationService.login(credentialsDto.getUsername(), credentialsDto.getPassword())
                .orElseThrow(() -> new WrongPasswordException("Wrong username and/or password"));
        return Collections.singletonMap("bearer", token);
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> registerUser(@RequestBody UserDto userDto) {
        if (!userDto.getPassword().equals(userDto.getPasswordConfirm())){
            throw new IllegalArgumentsPassed("Пароли не совпадают");
        }
        return Collections.singletonMap("bearer", userService.createUser(userDto).toString());
    }

}
