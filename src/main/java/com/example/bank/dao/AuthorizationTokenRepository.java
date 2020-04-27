package com.example.bank.dao;

import com.example.bank.entity.AuthorizationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthorizationTokenRepository extends JpaRepository<AuthorizationToken, Long> {

    AuthorizationToken findAuthorizationTokenByToken(UUID token);

}
