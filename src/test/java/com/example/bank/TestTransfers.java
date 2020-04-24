package com.example.bank;

import com.example.bank.dto.CardDTO;
import com.example.bank.dto.response.ErrorRequestDTO;
import com.example.bank.dto.response.VerifyTransferDTO;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTransfers extends AbstractTest{

    Logger logger = LoggerFactory.getLogger(TestTransfers.class);

    @Test
    void testCreateDepositTransfer() {
        RegisterUser user2 = registerRandomUser();
        CardDTO cardFrom = sendCreateCardRequest(registeredUser.bearer);
        CardDTO cardTo = sendCreateCardRequest(user2.bearer);

        String s = sendDeposit(cardFrom.getCardId(), 1234, registeredUser.bearer, HttpStatus.OK);
        assertEquals(1234, parseJson(s, CardDTO.class).getAmount());

        s = sendTransferRequest(cardFrom.getCardId(), cardTo.getCardId(),
                100, HttpStatus.OK, registeredUser.bearer);
        VerifyTransferDTO verifyTransferDTO = parseJson(s, VerifyTransferDTO.class);

        logger.info("//////////-------" + verifyTransferDTO.getPrincipal() + " sum: "
                + verifyTransferDTO.getAmount() +  "-------////////////");

        sendCompleteTransfer(verifyTransferDTO.getToken(), HttpStatus.OK, registeredUser.bearer);
        assertEquals(1134, getCardBalance(cardFrom.getCardId(), registeredUser.bearer));
        assertEquals(100, getCardBalance(cardTo.getCardId(), user2.bearer));
        deleteUser(user2.bearer);
    }

    @Test
    void testCreateDepositTransferWithNegativeAmount() {
        try (RegisterUser user2 = registerRandomUser()) {
            CardDTO cardFrom = sendCreateCardRequest(registeredUser.bearer);
            CardDTO cardTo = sendCreateCardRequest(user2.bearer);

            String s = sendDeposit(cardFrom.getCardId(), 1234, registeredUser.bearer, HttpStatus.OK);
            assertEquals(1234, parseJson(s, CardDTO.class).getAmount());

            s = sendTransferRequest(cardFrom.getCardId(), cardTo.getCardId(),
                    -100, HttpStatus.BAD_REQUEST, registeredUser.bearer);
            ErrorRequestDTO errorRequestDTO = parseJson(s, ErrorRequestDTO.class);
            logger.debug(errorRequestDTO.toString());
            deleteUser(user2.bearer);
        }
    }

    @Test
    void testCreateDepositTransferWithEqualBalanceAmount() {
        try (RegisterUser user2 = registerRandomUser()) {
            CardDTO cardFrom = sendCreateCardRequest(registeredUser.bearer);
            CardDTO cardTo = sendCreateCardRequest(user2.bearer);

            String s = sendDeposit(cardFrom.getCardId(), 1234, registeredUser.bearer, HttpStatus.OK);
            assertEquals(1234, parseJson(s, CardDTO.class).getAmount());

            s = sendTransferRequest(cardFrom.getCardId(), cardTo.getCardId(),
                    1234, HttpStatus.OK, registeredUser.bearer);
            logger.debug(s);
            deleteUser(user2.bearer);
        }
    }

    @Test
    void testCreateDepositTransferWithLessThanBalanceAmount() {
        try (RegisterUser user2 = registerRandomUser()) {
            CardDTO cardFrom = sendCreateCardRequest(registeredUser.bearer);
            CardDTO cardTo = sendCreateCardRequest(user2.bearer);

            String s = sendDeposit(cardFrom.getCardId(), 1234, registeredUser.bearer, HttpStatus.OK);
            assertEquals(1234, parseJson(s, CardDTO.class).getAmount());

            s = sendTransferRequest(cardFrom.getCardId(), cardTo.getCardId(),
                    2000, HttpStatus.BAD_REQUEST, registeredUser.bearer);
            ErrorRequestDTO errorRequestDTO = parseJson(s, ErrorRequestDTO.class);
            logger.debug(errorRequestDTO.toString());
        }
    }
}
