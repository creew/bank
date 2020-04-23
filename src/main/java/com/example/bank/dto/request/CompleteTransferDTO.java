package com.example.bank.dto.request;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class CompleteTransferDTO implements Serializable {

    private static final long serialVersionUID = 7417436578307970966L;

    @NotNull
    private String token;

    public String getToken() {
        return token;
    }

    public CompleteTransferDTO() {
    }

    public CompleteTransferDTO(String token) {
        this.token = token;
    }
}
