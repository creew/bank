package com.example.bank.dao;

import com.example.bank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>,
        TransactionRepositoryCustom {

}
