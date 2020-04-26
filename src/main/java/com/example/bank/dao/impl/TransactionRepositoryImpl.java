package com.example.bank.dao.impl;

import com.example.bank.dao.TransactionRepositoryCustom;
import com.example.bank.entity.Card;
import com.example.bank.entity.Card_;
import com.example.bank.entity.Transaction;
import com.example.bank.entity.Transaction_;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class TransactionRepositoryImpl implements TransactionRepositoryCustom {

    private final EntityManager em;

    public TransactionRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Transaction> fetchAllTransferByUserToUser(Long userId, Long userToId, Long amountFrom, Long amountTo, Date timeFrom, Date timeTo) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);

        Root<Transaction> book = cq.from(Transaction.class);
        List<Predicate> predicates = new ArrayList<>();

        Join<Transaction, Card> cardFromJoin = book.join(Transaction_.CARD_FROM, JoinType.LEFT);
        Join<Transaction, Card> cardToJoin = book.join(Transaction_.CARD_TO, JoinType.LEFT);

        Predicate predicateFromUser = cb.equal(cardFromJoin.get(Card_.FK_USER_ID), userId);
        Predicate predicateToUser = cb.equal(cardToJoin.get(Card_.FK_USER_ID), userId);
        predicates.add(cb.or(predicateFromUser, predicateToUser));

        if (userToId != null) {
            predicates.add(cb.equal(cardToJoin.get(Card_.FK_USER_ID), userToId));
        }

        if (amountFrom != null) {
            predicates.add(cb.ge(book.get(Transaction_.AMOUNT), amountFrom));
        }
        if (amountTo != null) {
            predicates.add(cb.le(book.get(Transaction_.AMOUNT), amountTo));
        }
        if (timeFrom != null) {
            predicates.add(cb.greaterThanOrEqualTo(book.get(Transaction_.TIME_EXECUTED), timeFrom));
        }
        if (timeTo != null) {
            predicates.add(cb.lessThanOrEqualTo(book.get(Transaction_.TIME_EXECUTED), timeTo));
        }
        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }
}
