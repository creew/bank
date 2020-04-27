package com.example.bank.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "TRANSACTIONS")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "AMOUNT")
    private Long amount;

    @Column(name = "TIME_EXECUTED")
    private Date timeExecuted;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_CARD_FROM_ID")
    private Card cardFrom;

    @Column(name = "FK_CARD_FROM_ID", insertable = false, updatable = false)
    private Long fkCardIdFrom;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_CARD_TO_ID")
    private Card cardTo;

    @Column(name = "FK_CARD_TO_ID", insertable = false, updatable = false)
    private Long fkCardIdTo;

    public Long getId() {
        return id;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Date getTimeExecuted() {
        return timeExecuted;
    }

    public void setTimeExecuted(Date timeExecuted) {
        this.timeExecuted = timeExecuted;
    }

    public Card getCardFrom() {
        return cardFrom;
    }

    public void setCardFrom(Card cardFrom) {
        this.cardFrom = cardFrom;
    }

    public Card getCardTo() {
        return cardTo;
    }

    public void setCardTo(Card cardTo) {
        this.cardTo = cardTo;
    }

    public Long getFkCardIdFrom() {
        return fkCardIdFrom;
    }

    public Long getFkCardIdTo() {
        return fkCardIdTo;
    }
}
