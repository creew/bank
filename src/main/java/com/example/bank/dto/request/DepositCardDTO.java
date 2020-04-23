package com.example.bank.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;

public class DepositCardDTO implements Serializable {

    private static final long serialVersionUID = -1648325318034466412L;

    @NotNull
    @Positive
    private Long amount;

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public DepositCardDTO(Long amount) {
        this.amount = amount;
    }

    public DepositCardDTO() {
    }
}
