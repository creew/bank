package com.example.bank.service.jooq;

import com.example.bank.dto.response.CardDTO;
import com.example.bank.dto.request.CompleteTransferDTO;
import com.example.bank.dto.response.VerifyTransferDTO;
import com.example.bank.entity.JooqOperationException;
import com.example.bank.entity.Transfer;
import com.example.bank.service.TransfersService;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.example.bank.jooq.tables.Transfers.TRANSFERS;

@Service
public class TransfersServiceJooq implements TransfersService {

    private final DSLContext dslContext;

    public TransfersServiceJooq(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public VerifyTransferDTO createNewTransfer(int cardFrom, int cardTo, long amount) {
        UUID uuid = UUID.randomUUID();
        int rows = dslContext.insertInto(TRANSFERS)
                .columns(TRANSFERS.FK_CARD_FROM_ID, TRANSFERS.FK_CARD_TO_ID, TRANSFERS.AMOUNT,
                        TRANSFERS.EXECUTED, TRANSFERS.TIME_CREATED, TRANSFERS.TIME_EXPIRATION,
                        TRANSFERS.TOKEN)
                .values(cardFrom, cardTo, amount, false, LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(5L), uuid.toString())
                .execute();
        if (rows != 1) {
            throw new JooqOperationException();
        }
        return new VerifyTransferDTO();
    }

    @Override
    public Optional<Transfer> findTransferByToken(String token) {
        return Optional.empty();
    }

    @Override
    public void setTransferComplete(Transfer transfer) {

    }

    @Override
    public VerifyTransferDTO createVerifyRequest(int userFromId, int cardIdFrom, int cardIdTo, long amount) {
        return null;
    }

    @Override
    public CardDTO completeTransfer(int userFromId, CompleteTransferDTO completeTransferDto) {
        return null;
    }
}
