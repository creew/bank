package com.example.bank;

import com.example.bank.dto.response.CardDTO;
import com.example.bank.dto.response.ErrorRequestDTO;
import com.example.bank.dto.response.VerifyTransferDTO;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTransfers extends AbstractTest {

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

        logger.info("//////////-------{} sum: {} -------////////////", verifyTransferDTO.getPrincipal(),
                verifyTransferDTO.getAmount());

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

    @RepeatedTest(100)
    void testConcurrentTransferConfirmation() throws InterruptedException {
        try (RegisterUser user2 = registerRandomUser()) {
            CardDTO cardFrom = sendCreateCardRequest(registeredUser.bearer);
            CardDTO cardTo = sendCreateCardRequest(user2.bearer);

            String s = sendDeposit(cardFrom.getCardId(), 100, registeredUser.bearer, HttpStatus.OK);
            assertEquals(100, parseJson(s, CardDTO.class).getAmount());

            List<String> transferTokens = new ArrayList<>();
            for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
                String transferRequestResult = sendTransferRequest(cardFrom.getCardId(), cardTo.getCardId(),
                        100 - i, HttpStatus.OK, registeredUser.bearer);
                VerifyTransferDTO verifyTransferDTO = parseJson(transferRequestResult, VerifyTransferDTO.class);
                transferTokens.add(verifyTransferDTO.getToken());
            }
            CountDownLatch start = new CountDownLatch(1);
            CountDownLatch finish = new CountDownLatch(transferTokens.size());
            ExecutorService executorService = Executors.newCachedThreadPool();
            AtomicInteger successCounter = new AtomicInteger(0);
            for (String transferToken : transferTokens) {
                executorService.submit(() -> {
                    try {
                        logger.info("Thread {} started at {}", Thread.currentThread().getId(), new Date());
                        start.await();
                        ResponseEntity<String> responseEntity= sendCompleteTransferNoValidate(transferToken, registeredUser.bearer);
                        if (responseEntity.getStatusCode() == HttpStatus.OK) {
                            successCounter.getAndIncrement();
                        } else {
                            logger.info("Wrong status: {}", responseEntity.getBody());
                        }
                    } catch (Exception ex) {
                        // ignoring exceptions
                    } finally {
                        finish.countDown();
                    }
                });
            }
            start.countDown();
            finish.await();
           // assertEquals(transferTokens.size(), successCounter.get());
            System.out.println("Number of succeeded calls: " + successCounter.get());
            CardDTO fromCard = getCard(registeredUser.bearer);
            CardDTO toCard = getCard(user2.bearer);
            printBalances(fromCard, toCard);
            assertEquals(100L, fromCard.getAmount() + toCard.getAmount(), "Illegal total sum");
        }
    }

    private CardDTO getCard(String bearer) {
        ResponseEntity<String> responseEntity = executeExchange("/cards", bearer, HttpMethod.GET);
        CardDTO[] cards = parseJson(responseEntity.getBody(), CardDTO[].class);
        return cards[0];
    }

    private void printBalances(CardDTO fromCard, CardDTO toCard) {
        logger.info("Card id {}, balance: {}", fromCard.getCardId(), fromCard.getAmount());
        logger.info("Card id {}, balance: {}", toCard.getCardId(), toCard.getAmount());
    }
}
