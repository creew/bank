package com.example.bank.service.jooq;

import com.example.bank.dto.response.CardDTO;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.jooq.tables.records.CardsRecord;
import com.example.bank.service.CardsService;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.bank.jooq.tables.Cards.CARDS;

@Service
public class CardsServiceJooq implements CardsService {

    private final DSLContext dslContext;

    public CardsServiceJooq(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    private static class CardDtoMapper implements RecordMapper<CardsRecord, CardDTO> {
        @Override
        public CardDTO map(CardsRecord record) {
            return new CardDTO(record.getId(), record.getAmount());
        }
    }

    private CardDTO getCardDtoById(int id) {
        return dslContext.selectFrom(CARDS)
                .where(CARDS.ID.eq(id))
                .fetchOne(new CardDtoMapper());
    }

    @Override
    public CardDTO deposit(Integer cardId, Long amount) {
        Long prevAmount = dslContext.select(CARDS.AMOUNT)
                .from(CARDS)
                .where(CARDS.ID.eq(cardId))
                .fetchOne().component1();
        if (prevAmount == null)
            throw new IllegalArgumentsPassed("Wrong card with card id: " + cardId + " passed");
        dslContext.update(CARDS)
                .set(CARDS.AMOUNT, amount + prevAmount)
                .where(CARDS.ID.eq(cardId))
                .execute();
        return getCardDtoById(cardId);
    }

    @Override
    public Optional<CardDTO> checkIsUsersCard(int userId, int cardId) {
        CardDTO cardDTO = dslContext.selectFrom(CARDS)
                .where(CARDS.ID.eq(cardId))
                .and(CARDS.FK_USER_ID.eq(userId))
                .fetchOne(new CardDtoMapper());
        return Optional.ofNullable(cardDTO);
    }

    @Override
    public List<CardDTO> getAllUserCard(int userId) {
        return dslContext.selectFrom(CARDS)
                .where(CARDS.FK_USER_ID.eq(userId))
                .fetch(new CardDtoMapper());
    }

    @Override
    public void deleteCardById(int cardId) {
        dslContext.deleteFrom(CARDS)
                .where(CARDS.ID.eq(cardId))
                .execute();
    }

    @Override
    public CardDTO createCard(int userId) {
        CardsRecord cardsRecord = dslContext.insertInto(CARDS, CARDS.AMOUNT, CARDS.FK_USER_ID)
                .values(0L, userId)
                .returning(CARDS.ID)
                .fetchOne();
        return new CardDTO(cardsRecord.getId(), 0L);
    }
}
