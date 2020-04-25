package com.example.bank.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "CARDS")
public class Card implements Serializable {

    private static final long serialVersionUID = 3948962228374042020L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer cardId;

    @Column(name="AMOUNT", nullable = false)
    private Long amount;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "FK_USER_ID",  nullable = false)
    private User user;

    public Card() {
    }

    public Card(User user, Long amount) {
        this.user = user;
        this.amount = amount;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
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

}
