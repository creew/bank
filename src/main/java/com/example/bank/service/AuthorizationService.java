package com.example.bank.service;

import com.example.bank.dao.AuthorizationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationService {

    @Autowired
    private AuthorizationTokenRepository authorizationTokenRepository;

    public void deleteAuthorizationToken(Long id) {
        authorizationTokenRepository.deleteById(id);
    }
}
