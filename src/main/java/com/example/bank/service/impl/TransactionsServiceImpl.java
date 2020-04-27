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

    private Date getDateFromUnixTime(Long time) {
        if (time != null) {
            return new Date(time);
        }
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<TransactionDTO> getTransactionsOfUser(Long userId, Long cardIdTo, Long amountFrom,
                                                      Long amountTo, Long dateFrom, Long dateTo) {
        List<Transaction> transactions = transactionRepository.fetchAllTransferByUser(
                userId, cardIdTo, amountFrom, amountTo,
                getDateFromUnixTime(dateFrom), getDateFromUnixTime(dateTo));
        return transactions.stream()
                .map(TransactionDTO::fromTransfer)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<TransactionDTO> getTransactionsOfCard(Long cardId, Long cardIdTo, Long amountFrom,
                                                      Long amountTo, Long dateFrom, Long dateTo) {
        List<Transaction> transactions = transactionRepository.fetchAllTransferByCard(
                cardId, cardIdTo, amountFrom, amountTo,
                getDateFromUnixTime(dateFrom), getDateFromUnixTime(dateTo));
        return transactions.stream()
                .map(TransactionDTO::fromTransfer)
                .collect(Collectors.toList());    }
}
