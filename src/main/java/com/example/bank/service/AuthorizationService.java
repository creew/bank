package com.example.bank.service;

import com.example.bank.dao.AuthorizationTokenRepository;
import com.example.bank.dto.UserDTO;
import com.example.bank.entity.AuthorizationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthorizationService {

    @Autowired
    private AuthorizationTokenRepository authorizationTokenRepository;

    public void deleteAuthorizationToken(Long id) {
        authorizationTokenRepository.deleteById(id);
    }

    @Transactional
    public Optional<UserDTO> getUserFromAuthorizationToken(String token) {
        AuthorizationToken authorizationToken = authorizationTokenRepository.findAuthorizationTokenByToken(token);
        if (authorizationToken != null && !authorizationToken.hasExpired()) {
            return Optional.of(UserDTO.ofUser(authorizationToken.getUser()));
        }
        return Optional.empty();
    }

}
