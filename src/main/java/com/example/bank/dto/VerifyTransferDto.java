package com.example.bank.dto;

public class VerifyTransferDto {

    private String principal;

    private long amount;

    private String token;

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public VerifyTransferDto(String principal, long amount, String token) {
        this.principal = principal;
        this.amount = amount;
        this.token = token;
    }
}

