package com.example.bank.dto;

import com.example.bank.entity.Card;

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

    public static CardDto fromCard(Card card) {
        CardDto cardDto = new CardDto();
        cardDto.cardId = card.getCardId();
        cardDto.amount = card.getAmount();
        return cardDto;
    }
}
