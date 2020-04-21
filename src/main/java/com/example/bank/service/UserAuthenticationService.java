package com.example.bank.service;

import com.example.bank.entity.User;

import java.util.Optional;

public interface UserAuthenticationService {

    Optional<String> login(String username, String password);

    Optional<User> findByNameToken(String userId, String token);

    void logout(User user);

}
