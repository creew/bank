package com.example.bank.service.jooq;

import com.example.bank.dto.response.TransactionDTO;
import com.example.bank.entity.Card;
import com.example.bank.jooq.tables.records.TransactionsRecord;
import com.example.bank.service.TransactionsService;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.example.bank.jooq.tables.Cards.CARDS;
import static com.example.bank.jooq.tables.Transactions.TRANSACTIONS;

@Service
public class TransactionsServiceJooq implements TransactionsService {

    private final DSLContext dslContext;

    public TransactionsServiceJooq(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    private static class TransactionDTOMapper implements RecordMapper<TransactionsRecord, TransactionDTO> {
        @Override
        public TransactionDTO map(TransactionsRecord record) {
            return new TransactionDTO(dateFromLocalDate(record.getTimeExecuted()), record.getAmount(),
                    "", "");
        }
    }

    private static Date dateFromLocalDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone( ZoneId.systemDefault()).toInstant());
    }

    @Override
    public List<TransactionDTO> getTransactionsOfUser(int userIdFrom, Long amountFrom, Long amountTo,
                                                      Date dateFrom, Date dateTo) {
        Result<Record> fetch = dslContext.select()
                .from(TRANSACTIONS)
                .join(CARDS)
                .on(CARDS.ID.eq(TRANSACTIONS.FK_CARD_FROM_ID))
                .join(CARDS)
                .on(CARDS.ID.eq(TRANSACTIONS.FK_CARD_TO_ID))
                .fetch();
        return Collections.emptyList();
    }

    @Override
    public List<TransactionDTO> getTransactionsOfUserToUser(int cardIdFrom, Long cardIdTo, Long amountFrom,
                                                            Long amountTo, Date dateFrom, Date dateTo) {
        return null;
    }

    @Override
    public void saveTransaction(Card cardFrom, Card cardTo, Long amount, Date date) {

    }
}
