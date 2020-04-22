package com.example.bank.dto;

import java.io.Serializable;

public class CompleteTransferDTO implements Serializable {

    private static final long serialVersionUID = 7417436578307970966L;

    private String token;

    public String getToken() {
        return token;
    }
}
