package com.example.bank.service.impl;

import com.example.bank.dao.TransactionRepository;
import com.example.bank.dto.response.TransactionDTO;
import com.example.bank.entity.Card;
import com.example.bank.entity.Transaction;
import com.example.bank.service.TransactionsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionsServiceImpl implements TransactionsService {

    private final TransactionRepository transactionRepository;

    public TransactionsServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    @Override
    public void saveTransaction(Card cardFrom, Card cardTo, Long amount, Date date) {
        Transaction transaction = new Transaction();
        transaction.setCardFrom(cardFrom);
        transaction.setCardTo(cardTo);
        transaction.setAmount(amount);
        transaction.setTimeExecuted(date);
        transactionRepository.saveAndFlush(transaction);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TransactionDTO> getTransactionsOfUserToUser(Long cardIdFrom, Long cardIdTo, Long amountFrom, Long amountTo, Date dateFrom, Date dateTo) {
        List<Transaction> transactions = transactionRepository.fetchAllTransferByUserToUser(
                cardIdFrom, cardIdTo, amountFrom, amountTo, dateFrom, dateTo);
        return transactions.stream()
                .map(TransactionDTO::fromTransfer)
                .collect(Collectors.toList());
    }
}
