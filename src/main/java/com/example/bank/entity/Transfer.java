package com.example.bank.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "transfers")
public class Transfer implements Serializable {

    private static final Integer DEFAULT_TIME_TO_LIVE_IN_SECONDS = (60 * 5);

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "ID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "AMOUNT")
    private Long amount;

    @Column(name = "TIME_CREATED")
    private Date timeCreated;

    @Column(name = "TIME_EXPIRATION")
    private Date timeExpiration;

    @Column(name = "EXECUTED")
    private Boolean executed;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_CARD_FROM_ID")
    private Card cardFrom;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_CARD_TO_ID")
    private Card cardTo;

    public Transfer() {
    }

    public Transfer(Card cardFrom, Integer timeToLiveInSeconds) {
        this.cardFrom = cardFrom;
        this.timeCreated = new Date();
        this.executed = false;
        this.timeExpiration = new Date(System.currentTimeMillis() + (timeToLiveInSeconds * 1000L));
    }

    public Transfer(Card cardFrom) {
        this(cardFrom, DEFAULT_TIME_TO_LIVE_IN_SECONDS);
    }

    public Transfer(Card cardFrom, Card cardTo, Long amount) {
        this(cardFrom);
        this.amount = amount;
        this.cardTo = cardTo;
    }

    public boolean hasExpired() {
        return this.timeExpiration != null && this.timeExpiration.before(new Date());
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Date getTimeExpiration() {
        return timeExpiration;
    }

    public void setTimeExpiration(Date timeExpiration) {
        this.timeExpiration = timeExpiration;
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

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean active) {
        this.executed = active;
    }

    public UUID getId() {
        return id;
    }
}
