package com.example.bank.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "transfers")
public class Transfer implements Serializable {

    private static final Integer DEFAULT_TIME_TO_LIVE_IN_SECONDS = (60 * 5);

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "token", length = 36)
    private String token;

    @Column(name = "time_created")
    private Date timeCreated;

    @Column(name = "time_expiration")
    private Date timeExpiration;

    @Column(name = "executed")
    private Boolean executed;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_card_from_id")
    private Card cardFrom;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_card_to_id")
    private Card cardTo;

    public Transfer() {
    }

    public Transfer(Card cardFrom, Integer timeToLiveInSeconds) {
        this.token = UUID.randomUUID().toString();
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
}
