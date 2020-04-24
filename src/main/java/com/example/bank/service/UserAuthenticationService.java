package com.example.bank.service;

import com.example.bank.dto.UserDTO;

import java.util.Optional;

public interface UserAuthenticationService {

    Optional<String> login(String username, String password);

    Optional<UserDTO> findByToken(String token);

    void logout(UserDTO user);

}
