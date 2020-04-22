package com.example.bank.dto;

import com.example.bank.entity.Card;

import java.io.Serializable;

public class CardDTO implements Serializable {

    private static final long serialVersionUID = -1986157594950218606L;

    private long cardId;

    private long amount;

    public long getCardId() {
        return cardId;
    }

    public long getAmount() {
        return amount;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public static CardDTO fromCard(Card card) {
        CardDTO cardDto = new CardDTO();
        cardDto.cardId = card.getCardId();
        cardDto.amount = card.getAmount();
        return cardDto;
    }
}
