package com.example.bank.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "CARDS")
public class Card implements Serializable {

    private static final long serialVersionUID = 3948962228374042020L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CARD_ID")
    private Long cardId;

    @Column(name="AMOUNT", nullable = false)
    private Long amount;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_user_id",  nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "cardFrom", cascade = CascadeType.ALL)
    private VerificationToken verificationToken;

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

    public VerificationToken getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(VerificationToken verificationToken) {
        this.verificationToken = verificationToken;
    }
}
