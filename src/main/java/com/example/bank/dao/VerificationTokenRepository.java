package com.example.bank.dao;

import com.example.bank.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findVerificationTokenByToken(String token);

    @Modifying
    @Query("DELETE FROM VerificationToken u WHERE u.cardFrom.cardId = ?1")
    void deletePreviousTokens(Long id);
}
