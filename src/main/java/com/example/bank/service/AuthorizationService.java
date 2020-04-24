package com.example.bank.service;

import com.example.bank.dto.UserDTO;

import java.util.Optional;

public interface AuthorizationService {

    void deleteAuthorizationToken(Long id);

    Optional<UserDTO> getUserFromAuthorizationToken(String token);

}
