package com.example.bank.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;

public class RequestTransferDTO implements Serializable {

    private static final long serialVersionUID = -6416482809059557108L;

    @NotNull
    @JsonProperty("card_id_to")
    private Long cardIdTo;

    @NotNull
    @Positive
    @JsonProperty("amount")
    private Long amount;

    public long getCardIdTo() {
        return cardIdTo;
    }

    public long getAmount() {
        return amount;
    }

    public RequestTransferDTO() {
    }

    public RequestTransferDTO(long cardIdTo, long amount) {
        this.cardIdTo = cardIdTo;
        this.amount = amount;
    }
}
