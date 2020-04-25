package com.example.bank.dto;

import com.example.bank.entity.Card;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class CardDTO implements Serializable {

    private static final long serialVersionUID = -1986157594950218606L;

    private @NotNull Integer cardId;

    @NotNull
    private Long amount;

    public Integer getCardId() {
        return cardId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public CardDTO() {
    }

    public static CardDTO fromCard(Card card) {
        CardDTO cardDto = new CardDTO();
        cardDto.cardId = card.getCardId();
        cardDto.amount = card.getAmount();
        return cardDto;
    }

    public CardDTO(@NotNull Integer cardId, @NotNull Long amount) {
        this.cardId = cardId;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "CardDTO{" +
                "cardId=" + cardId +
                ", amount=" + amount +
                '}';
    }
}
