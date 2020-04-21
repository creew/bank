package com.example.bank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestTransferDto {
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
}
