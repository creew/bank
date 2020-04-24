package com.example.bank.dto;

import com.example.bank.entity.Card;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class CardDTO implements Serializable {

    private static final long serialVersionUID = -1986157594950218606L;

    @NotNull
    private Long cardId;

    @NotNull
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

    public static CardDTO fromCard(Card card) {
        CardDTO cardDto = new CardDTO();
        cardDto.cardId = card.getCardId();
        cardDto.amount = card.getAmount();
        return cardDto;
    }

    @Override
    public String toString() {
        return "CardDTO{" +
                "cardId=" + cardId +
                ", amount=" + amount +
                '}';
    }
}
