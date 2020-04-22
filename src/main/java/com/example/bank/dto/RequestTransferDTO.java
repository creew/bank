package com.example.bank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestTransferDTO {

    @JsonProperty("card_id_to")
    private long cardIdTo;

    @JsonProperty("amount")
    private long amount;

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
