package com.example.bank.dto;

public class CardDto {

    private Long cardId;

    private Long amount;

    public Long getCardId() {
        return cardId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
