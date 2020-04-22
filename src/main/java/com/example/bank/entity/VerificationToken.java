package com.example.bank.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "verification_token")
public class VerificationToken implements Serializable {

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

    @Column(name = "active")
    private Boolean active;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_card_from_id")
    private Card cardFrom;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_card_to_id")
    private Card cardTo;

    public VerificationToken() {
    }

    public VerificationToken(Card cardFrom, Integer timeToLiveInSeconds) {
        this.token = UUID.randomUUID().toString();
        this.cardFrom = cardFrom;
        this.timeCreated = new Date();
        this.active = true;
        this.timeExpiration = new Date(System.currentTimeMillis() + (timeToLiveInSeconds * 1000L));
    }

    public VerificationToken(Card cardFrom) {
        this(cardFrom, DEFAULT_TIME_TO_LIVE_IN_SECONDS);
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
