package com.example.bank.dto.response;

import com.example.bank.entity.Card;
import com.example.bank.entity.Transaction;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

public class TransactionDTO implements Serializable {

    private static final long serialVersionUID = -3587601335790657712L;

    @JsonProperty("executed_date")
    private Date executedDate;

    @JsonProperty("amount")
    private Long amount;

    @JsonProperty("credentials_to")
    private String credentialsTo;

    @JsonProperty("credentials_from")
    private String credentialsFrom;

    public Date getExecutedDate() {
        return executedDate;
    }

    public void setExecutedDate(Date executedDate) {
        this.executedDate = executedDate;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCredentialsTo() {
        return credentialsTo;
    }

    public void setCredentialsTo(String credentialsTo) {
        this.credentialsTo = credentialsTo;
    }

    public TransactionDTO() {
    }

    public TransactionDTO(Date executedDate, Long amount, String credentialsTo, String credentialsFrom) {
        this.executedDate = executedDate;
        this.amount = amount;
        this.credentialsTo = credentialsTo;
        this.credentialsFrom = credentialsFrom;
    }

    public static TransactionDTO fromTransfer(Transaction transaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.executedDate = transaction.getTimeExecuted();
        transactionDTO.amount = transaction.getAmount();
        Card cardTo = transaction.getCardTo();
        transactionDTO.credentialsTo = cardTo == null ? "Unknown user" : cardTo.getUser().getPrincipal();
        Card cardFrom = transaction.getCardFrom();
        transactionDTO.credentialsFrom = cardFrom == null ? "Unknown user" : cardFrom.getUser().getPrincipal();
        return transactionDTO;
    }

    @Override
    public String toString() {
        return "TransactionDTO{" +
                "executedDate=" + executedDate +
                ", amount=" + amount +
                ", credentialsTo='" + credentialsTo + '\'' +
                ", credentialsFrom='" + credentialsFrom + '\'' +
                '}';
    }
}
