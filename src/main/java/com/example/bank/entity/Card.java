package com.example.bank.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "CARDS")
public class Card implements Serializable {

    private static final long serialVersionUID = 3948962228374042020L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long cardId;

    @Column(name="AMOUNT", nullable = false)
    private Long amount;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "FK_USER_ID",  nullable = false)
    private User user;

    @Column(name = "FK_USER_ID", insertable = false, updatable = false)
    private Long fkUserId;

    public Card() {
    }

    public Card(User user, Long amount) {
        this.user = user;
        this.amount = amount;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getFkUserId() {
        return fkUserId;
    }

    public void setFkUserId(Long fkUserId) {
        this.fkUserId = fkUserId;
    }
}
