package com.example.bank.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="AUTHORIZATION_TOKEN")
public class AuthorizationToken implements Serializable {

    private static final Integer DEFAULT_TIME_TO_LIVE_IN_SECONDS = (60 * 60 * 24 * 30);

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "ID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "TIME_CREATED")
    private Date timeCreated;

    @Column(name = "TIME_EXPIRATION")
    private Date timeExpiration;

    @JoinColumn(name = "FK_USER_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    public AuthorizationToken() {}

    public AuthorizationToken(User user) {
        this(user, DEFAULT_TIME_TO_LIVE_IN_SECONDS);
    }

    public AuthorizationToken(User user, Integer timeToLiveInSeconds) {
        this.user = user;
        this.timeCreated = new Date();
        this.timeExpiration = new Date(System.currentTimeMillis() + (timeToLiveInSeconds * 1000L));
    }

    public boolean hasExpired() {
        return this.timeExpiration != null && this.timeExpiration.before(new Date());
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Date getTimeExpiration() {
        return timeExpiration;
    }

    public void setTimeExpiration(Date expirationDate) {
        this.timeExpiration = expirationDate;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "AuthorizationToken{" +
                "id=" + id +
                ", timeCreated=" + timeCreated +
                ", timeExpiration=" + timeExpiration +
                ", user=" + user +
                '}';
    }
}
