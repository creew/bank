package com.example.bank.controller;

import com.example.bank.dto.request.CredentialsDTO;
import com.example.bank.dto.request.UserRegisterDTO;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.exception.WrongPasswordException;
import com.example.bank.service.UsersService;
import com.example.bank.service.UserAuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsersService usersService;

    private final UserAuthenticationService authenticationService;

    public AuthController(UsersService usersService,
                          UserAuthenticationService authenticationService) {
        this.usersService = usersService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signin")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> login(@RequestBody @Valid CredentialsDTO credentialsDto) {
        String token = authenticationService.login(credentialsDto.getUsername(), credentialsDto.getPassword())
                .orElseThrow(() -> new WrongPasswordException("Wrong username and/or password"));
        return Collections.singletonMap("bearer", token);
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> registerUser(@RequestBody @Valid UserRegisterDTO userRegisterDto) {
        if (!userRegisterDto.getPassword().equals(userRegisterDto.getPasswordConfirm())){
            throw new IllegalArgumentsPassed("Пароли не совпадают");
        }
        return Collections.singletonMap("bearer", usersService.createUser(userRegisterDto).getToken());
    }

}
