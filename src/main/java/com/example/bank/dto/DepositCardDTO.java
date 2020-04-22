package com.example.bank.dto;

public class DepositCardDTO {

    private long amount;

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public DepositCardDTO(long amount) {
        this.amount = amount;
    }

    public DepositCardDTO() {
    }
}
