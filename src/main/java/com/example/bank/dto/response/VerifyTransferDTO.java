package com.example.bank.dto.response;

import com.example.bank.entity.Transfer;

import java.io.Serializable;

public class VerifyTransferDTO implements Serializable {

    private static final long serialVersionUID = 3410991059459458849L;

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

    public static VerifyTransferDTO fromTransfer(Transfer transfer) {
        VerifyTransferDTO verifyTransferDTO = new VerifyTransferDTO();
        verifyTransferDTO.token = transfer.getToken().toString();
        verifyTransferDTO.amount = transfer.getAmount();
        verifyTransferDTO.principal = transfer.getCardTo().getUser().getPrincipal();
        return verifyTransferDTO;
    }
}

