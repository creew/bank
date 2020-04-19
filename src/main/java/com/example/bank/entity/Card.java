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
    @JoinColumn(name = "FK_CUSTOMER_ID",  nullable = false)
    private Customer customer;

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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
