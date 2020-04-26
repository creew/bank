package com.example.bank;

import com.example.bank.dto.response.CardDTO;
import com.example.bank.dto.response.TransactionDTO;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTransferInfo extends AbstractTest {

    Logger logger = LoggerFactory.getLogger(TestTransfers.class);

    TransactionDTO[] sendGetInfo(Map<String, String> params, HttpStatus expected, String bearer) {
        String url = getContextPath() + "/transactions" + ( params.size() == 0 ? "" : "?" +
                params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&")));
        HttpEntity<String> httpEntity = createHttpEntity(null, bearer);
        ResponseEntity<String> entity = restTemplate.exchange(url, HttpMethod.GET,
                httpEntity, String.class);
        assertEquals(expected, entity.getStatusCode());
        return parseJson(entity.getBody(), TransactionDTO[].class);
    }

    @Test
    void testAllInfo() {
        try (RegisterUser user2 = registerRandomUser()) {
            CardDTO card1 = sendCreateCardRequest(registeredUser.bearer);
            CardDTO card2 = sendCreateCardRequest(user2.bearer);
            sendDeposit(card1.getCardId(), 10000, registeredUser.bearer, HttpStatus.OK);
            createTransfer(card1.getCardId(), card2.getCardId(), 99, registeredUser.bearer);
            createTransfer(card1.getCardId(), card2.getCardId(), 99, registeredUser.bearer);
            createTransfer(card1.getCardId(), card2.getCardId(), 99, registeredUser.bearer);
            CardDTO newData = createTransfer(card1.getCardId(), card2.getCardId(), 99, registeredUser.bearer);
            logger.info(newData.toString());
            TransactionDTO[] infos = sendGetInfo(Collections.emptyMap(), HttpStatus.OK, registeredUser.bearer);
            logger.info(Arrays.toString(infos));
            assertEquals(5, infos.length);
        }
    }

    @Test
    void testAmountMoreThanInfo() {
        try (RegisterUser user2 = registerRandomUser()) {
            CardDTO card1 = sendCreateCardRequest(registeredUser.bearer);
            CardDTO card2 = sendCreateCardRequest(user2.bearer);
            sendDeposit(card1.getCardId(), 100000, registeredUser.bearer, HttpStatus.OK);
            createTransfer(card1.getCardId(), card2.getCardId(), 10, registeredUser.bearer);
            createTransfer(card1.getCardId(), card2.getCardId(), 50, registeredUser.bearer);
            createTransfer(card1.getCardId(), card2.getCardId(), 1000, registeredUser.bearer);
            CardDTO newData = createTransfer(card1.getCardId(), card2.getCardId(), 20000, registeredUser.bearer);
            logger.info(newData.toString());
            TransactionDTO[] infos = sendGetInfo(Collections.singletonMap("from_amount", "1000"),
                    HttpStatus.OK, registeredUser.bearer);
            logger.info(Arrays.toString(infos));
            assertEquals(3, infos.length);
        }
    }
}
