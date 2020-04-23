package com.example.bank.dao;

import com.example.bank.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllByUser_UserId(Long userId);

}
