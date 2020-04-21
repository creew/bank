package com.example.bank.dao;

import com.example.bank.entity.Card;
import com.example.bank.entity.User;
import com.example.bank.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {

}
