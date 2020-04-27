package com.example.bank.service;

import com.example.bank.dto.UserDTO;

import java.util.Optional;
import java.util.UUID;

public interface AuthorizationService {

    void deleteAuthorizationToken(UUID id);

    Optional<UserDTO> getUserFromAuthorizationToken(UUID token);

}
