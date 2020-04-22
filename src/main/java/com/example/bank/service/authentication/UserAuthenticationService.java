package com.example.bank.service.authentication;

import com.example.bank.dto.UserDTO;
import com.example.bank.entity.User;

import java.util.Optional;

public interface UserAuthenticationService {

    Optional<String> login(String username, String password);

    Optional<UserDTO> findByToken(String token);

    void logout(User user);

}
