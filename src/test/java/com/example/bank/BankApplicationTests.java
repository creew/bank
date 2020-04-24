package com.example.bank;

import com.example.bank.dto.CardDTO;
import com.example.bank.dto.request.CredentialsDTO;
import com.example.bank.dto.request.UserRegisterDTO;
import com.example.bank.dto.response.ErrorRequestDTO;
import com.example.bank.dto.response.VerifyTransferDTO;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BankApplicationTests extends AbstractTest{

    private Logger logger = LoggerFactory.getLogger(BankApplicationTests.class);

    @Test
    void testSignIn() {
        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(getContextPath() + "/auth/signin",
                new CredentialsDTO(registeredUser.login, registeredUser.password), JsonNode.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testGetAllCardsEmpty() {
        ResponseEntity<String> responseEntity = executeExchange("/cards", registeredUser.bearer,
                HttpMethod.GET);

        logger.debug(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testGetAllCardsNotEmpty() {
        CardDTO card1 = sendCreateCardRequest(registeredUser.bearer);
        CardDTO card2 = sendCreateCardRequest(registeredUser.bearer);
        String s = sendDeposit(card1.getCardId(), 1234, registeredUser.bearer, HttpStatus.OK);
        assertEquals(1234, parseJson(s, CardDTO.class).getAmount());
        ResponseEntity<String> responseEntity = executeExchange("/cards", registeredUser.bearer,
                HttpMethod.GET);
        CardDTO[] cards = parseJson(responseEntity.getBody(), CardDTO[].class);
        logger.debug(Arrays.toString(cards));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testCreateNewCard() {
        ResponseEntity<String> responseEntity = executeExchange("/cards", registeredUser.bearer,
                HttpMethod.PUT);
        logger.debug(responseEntity.getBody());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        CardDTO cardDTO = parseJson(responseEntity.getBody(), CardDTO.class);
        assertEquals(0, cardDTO.getAmount());
    }

    @Test
    void testCreateDeposit() {
        CardDTO cardDTO = sendCreateCardRequest(registeredUser.bearer);
        String s = sendDeposit(cardDTO.getCardId(), 1234, registeredUser.bearer, HttpStatus.OK);
        assertEquals(1234, parseJson(s, CardDTO.class).getAmount());
    }

    @Test
    void testCreateDepositLessZero() {
        CardDTO cardDTO = sendCreateCardRequest(registeredUser.bearer);
        String s = sendDeposit(cardDTO.getCardId(), -1234, registeredUser.bearer, HttpStatus.BAD_REQUEST);
        ErrorRequestDTO errorRequestDTO = parseJson(s, ErrorRequestDTO.class);
        logger.debug(errorRequestDTO.toString());
    }

    @Test
    void testCreateDepositDoubleWithdraw() {
        try (AbstractTest.RegisterUser user2 = registerRandomUser()) {
            CardDTO cardFrom = sendCreateCardRequest(registeredUser.bearer);
            CardDTO cardTo = sendCreateCardRequest(user2.bearer);

            String s = sendDeposit(cardFrom.getCardId(), 1234, registeredUser.bearer, HttpStatus.OK);
            assertEquals(1234, parseJson(s, CardDTO.class).getAmount());

            s = sendTransferRequest(cardFrom.getCardId(), cardTo.getCardId(),
                    200, HttpStatus.OK, registeredUser.bearer);
            VerifyTransferDTO verifyTransferDTO = parseJson(s, VerifyTransferDTO.class);

            sendCompleteTransfer(verifyTransferDTO.getToken(), HttpStatus.OK, registeredUser.bearer);
            s = sendCompleteTransfer(verifyTransferDTO.getToken(), HttpStatus.BAD_REQUEST, registeredUser.bearer);

            ErrorRequestDTO errorRequestDTO = parseJson(s, ErrorRequestDTO.class);
            logger.debug(errorRequestDTO.toString());
            assertEquals(1034, getCardBalance(cardFrom.getCardId(), registeredUser.bearer));
        }
    }

    @Test
    void testCreateDepositDoubleRequest() {
        try (RegisterUser user2 = registerRandomUser()) {
            CardDTO cardFrom = sendCreateCardRequest(registeredUser.bearer);
            CardDTO cardTo = sendCreateCardRequest(user2.bearer);

            String s = sendDeposit(cardFrom.getCardId(), 1234, registeredUser.bearer, HttpStatus.OK);
            assertEquals(1234, parseJson(s, CardDTO.class).getAmount());

            s = sendTransferRequest(cardFrom.getCardId(), cardTo.getCardId(),
                    200, HttpStatus.OK, registeredUser.bearer);
            VerifyTransferDTO verifyTransferDTO = parseJson(s, VerifyTransferDTO.class);

            s = sendTransferRequest(cardFrom.getCardId(), cardTo.getCardId(),
                    300, HttpStatus.OK, registeredUser.bearer);
            VerifyTransferDTO verifyTransferDTO2 = parseJson(s, VerifyTransferDTO.class);

            sendCompleteTransfer(verifyTransferDTO.getToken(), HttpStatus.OK, registeredUser.bearer);
            s = sendCompleteTransfer(verifyTransferDTO2.getToken(), HttpStatus.OK, registeredUser.bearer);
            CardDTO returnedCard = parseJson(s, CardDTO.class);
            assertEquals(734, returnedCard.getAmount());
        }
    }

    @Nested
    class WithoutBeforeAfterEach {

        @Test
        void createUserWithNullValues() {
            UserRegisterDTO userRegisterDTO = new UserRegisterDTO("11", "22", "22", null, "22", "22");
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(getContextPath() + "/auth/signup",
                    userRegisterDTO, String.class);
            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        }

    }
}

