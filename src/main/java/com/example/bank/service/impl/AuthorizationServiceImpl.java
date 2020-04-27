package com.example.bank.service.impl;

import com.example.bank.dao.AuthorizationTokenRepository;
import com.example.bank.dto.UserDTO;
import com.example.bank.entity.AuthorizationToken;
import com.example.bank.service.AuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private final AuthorizationTokenRepository authorizationTokenRepository;

    public AuthorizationServiceImpl(AuthorizationTokenRepository authorizationTokenRepository) {
        this.authorizationTokenRepository = authorizationTokenRepository;
    }

    @Override
    public void deleteAuthorizationToken(UUID id) {
        authorizationTokenRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<UserDTO> getUserFromAuthorizationToken(UUID token) {
        AuthorizationToken authorizationToken = authorizationTokenRepository
                .findAuthorizationTokenById(token);
        if (authorizationToken != null && !authorizationToken.hasExpired()) {
            return Optional.of(UserDTO.fromUser(authorizationToken.getUser()));
        }
        return Optional.empty();
    }

}
