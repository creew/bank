package com.example.bank.dao;

import com.example.bank.entity.AuthorizationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizationTokenRepository extends JpaRepository<AuthorizationToken, Long> {

    AuthorizationToken findAuthorizationTokenByToken(String token);

}
