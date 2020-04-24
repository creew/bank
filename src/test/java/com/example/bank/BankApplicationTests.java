package com.example.bank;

import com.example.bank.dto.CardDTO;
import com.example.bank.dto.request.*;
import com.example.bank.dto.response.ErrorRequestDTO;
import com.example.bank.dto.response.VerifyTransferDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BankApplicationTests {

    private Logger logger = LoggerFactory.getLogger(BankApplicationTests.class);

    private static final String BASE_URL = "http://localhost:";

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @Value("${server.servlet.context-path}")
    private String servletContextPath;

    @Autowired
    public void setRestTemplate(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
        this.restTemplate.setErrorHandler(new MyErrorHandler());
    }

    private String getContextPath() {
        return BASE_URL + port + servletContextPath;
    }

    private RegisterUser registeredUser;

    public static class MyErrorHandler implements ResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse response) {
            return false;
        }

        @Override
        public void handleError(ClientHttpResponse response) {

        }
    }

    @BeforeEach
    void beforeEach() {
        registeredUser = registerRandomUser();
        assertEquals(HttpStatus.CREATED, registeredUser.responseEntity.getStatusCode());
    }

    @AfterEach
    void afterEach() {
        ResponseEntity<String> responseEntity = deleteUser(registeredUser.bearer);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    class RegisterUser implements AutoCloseable {
        String login;
        String password;
        String bearer;
        ResponseEntity<JsonNode> responseEntity;

        public RegisterUser() {
            this.login = Integer.toString((int)(Math.random() * 1_000_000_000));
            this.password = Integer.toString((int)(Math.random() * 1_000_000_000));
        }

        public RegisterUser(String login, String password) {
            this.login = login;
            this.password = password;
        }

        @Override
        public void close() {
            deleteUser(bearer);
        }
    }

    private RegisterUser registerNewUser(UserRegisterDTO userRegisterDTO) {
        RegisterUser registerUser = new RegisterUser(userRegisterDTO.getLogin(), userRegisterDTO.getPassword());
        registerUser.responseEntity = restTemplate.postForEntity(getContextPath() + "/auth/signup",
                userRegisterDTO, JsonNode.class);
        registerUser.bearer = Objects.requireNonNull(registerUser.responseEntity.getBody()).get("bearer").asText();
        return registerUser;
    }

    private RegisterUser registerRandomUser() {
        RegisterUser regUser = new RegisterUser();
        return registerNewUser(new UserRegisterDTO(regUser.login, regUser.password,
                "12", "12", "12", regUser.password));
    }

    static HttpHeaders createHeaders(String bearer){
        return new HttpHeaders() {{
            setBearerAuth(bearer);
            setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            setContentType(MediaType.APPLICATION_JSON);
        }};
    }

    private <T> HttpEntity<T> createHttpEntity(T body, String bearer)
    {
        HttpHeaders httpHeaders = createHeaders(bearer);
        return new HttpEntity<>(body, httpHeaders);
    }

    private <T> ResponseEntity<String> executeExchangeWithBody(String path, String bearer,
                                                               HttpMethod method, T body) {
        HttpEntity<T> httpEntity = createHttpEntity(body, bearer);
        return restTemplate.exchange(getContextPath() + path,
                method,
                httpEntity,
                String.class);
    }

    private ResponseEntity<String> executeExchange(String path, String bearer, HttpMethod method) {
        return executeExchangeWithBody(path, bearer, method, null);
    }

    private ResponseEntity<String> deleteUser(String bearer) {
        return executeExchange("/users", bearer, HttpMethod.DELETE);
    }

    private <T> T parseJson(String json, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private CardDTO sendCreateCardRequest(String bearer) {
        ResponseEntity<String> createCard = executeExchange("/cards", bearer,
                HttpMethod.PUT);
        assertEquals(HttpStatus.CREATED, createCard.getStatusCode());
        return parseJson(createCard.getBody(), CardDTO.class);
    }

    private long getCardBalance(long cardId, String bearer) {
        ResponseEntity<String> responseGetCard = executeExchange("/cards/" + cardId,
                bearer, HttpMethod.GET);
        assertEquals(HttpStatus.OK, responseGetCard.getStatusCode());
        CardDTO cardNew = parseJson(responseGetCard.getBody(), CardDTO.class);
        return cardNew.getAmount();
    }

    private String sendTransferRequest(long cardIdFrom, long cardIdTo, long amount,
                                       HttpStatus expected, String bearer) {
        RequestTransferDTO requestTransferDTO = new RequestTransferDTO(cardIdTo, amount);
        ResponseEntity<String> responseTransfer = executeExchangeWithBody("/transfer/" + cardIdFrom,
                bearer, HttpMethod.PUT, requestTransferDTO);
        assertEquals(expected, responseTransfer.getStatusCode());
        return responseTransfer.getBody();
    }

    private String sendCompleteTransfer(String token, HttpStatus status, String bearer) {
        CompleteTransferDTO completeTransferDTO = new CompleteTransferDTO(token);
        ResponseEntity<String> responseComplete = executeExchangeWithBody("/transfer",
                bearer, HttpMethod.PUT, completeTransferDTO);
        assertEquals(status, responseComplete.getStatusCode());
        return responseComplete.getBody();
    }

    private String sendDeposit(long cardId, long amount, String bearer, HttpStatus expected) {
        DepositCardDTO depositCardDTO = new DepositCardDTO(amount);
        ResponseEntity<String> responseDeposit = executeExchangeWithBody("/cards/" + cardId,
                bearer, HttpMethod.POST, depositCardDTO);
        assertEquals(expected, responseDeposit.getStatusCode());
        return responseDeposit.getBody();
    }

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
    void testCreateDepositTransfer() {
        RegisterUser user2 = registerRandomUser();
        CardDTO cardFrom = sendCreateCardRequest(registeredUser.bearer);
        CardDTO cardTo = sendCreateCardRequest(user2.bearer);

        String s = sendDeposit(cardFrom.getCardId(), 1234, registeredUser.bearer, HttpStatus.OK);
        assertEquals(1234, parseJson(s, CardDTO.class).getAmount());

        s = sendTransferRequest(cardFrom.getCardId(), cardTo.getCardId(),
                100, HttpStatus.OK, registeredUser.bearer);
        VerifyTransferDTO verifyTransferDTO = parseJson(s, VerifyTransferDTO.class);

        logger.debug("//////////-------" + verifyTransferDTO.getPrincipal() + " sum: "
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

    @Test
    void testCreateDepositDoubleWithdraw() {
        try (RegisterUser user2 = registerRandomUser()) {
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

