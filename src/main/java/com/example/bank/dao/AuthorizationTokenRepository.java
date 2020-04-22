package com.example.bank.dao;

import com.example.bank.entity.AuthorizationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorizationTokenRepository extends JpaRepository<AuthorizationToken, Long> {
    AuthorizationToken findAuthorizationTokenByToken(String token);
}
