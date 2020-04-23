package com.example.bank.dao;

import com.example.bank.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Transfer findTransferByToken(String token);

}
