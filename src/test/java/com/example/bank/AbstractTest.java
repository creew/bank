package com.example.bank;

import com.example.bank.dto.response.CardDTO;
import com.example.bank.dto.request.CompleteTransferDTO;
import com.example.bank.dto.request.DepositCardDTO;
import com.example.bank.dto.request.RequestTransferDTO;
import com.example.bank.dto.request.UserRegisterDTO;
import com.example.bank.dto.response.VerifyTransferDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractTest {

    @LocalServerPort
    protected int port;

    protected RestTemplate restTemplate;

    protected RegisterUser registeredUser;

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

    private static final String BASE_URL = "http://localhost:";

    @Value("${server.servlet.context-path}")
    protected String servletContextPath;

    @Autowired
    public void setRestTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.build();
        restTemplate.setErrorHandler(new MyErrorHandler());
        this.restTemplate = restTemplate;
    }

    protected String getContextPath() {
        return BASE_URL + port + servletContextPath;
    }

    public static class MyErrorHandler implements ResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse response) {
            return false;
        }

        @Override
        public void handleError(ClientHttpResponse response) {

        }
    }

    private RegisterUser registerNewUser(UserRegisterDTO userRegisterDTO) {
        RegisterUser registerUser = new RegisterUser(userRegisterDTO.getLogin(), userRegisterDTO.getPassword());
        registerUser.responseEntity = restTemplate.postForEntity(getContextPath() + "/auth/signup",
                userRegisterDTO, JsonNode.class);
        registerUser.bearer = Objects.requireNonNull(registerUser.responseEntity.getBody()).get("bearer").asText();
        return registerUser;
    }

    protected RegisterUser registerRandomUser() {
        RegisterUser regUser = new RegisterUser();
        return registerNewUser(new UserRegisterDTO(regUser.login, regUser.password,
                "12", "12", "12", regUser.password));
    }

    protected HttpHeaders createHeaders(String bearer){
        return new HttpHeaders() {{
            setBearerAuth(bearer);
            setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            setContentType(MediaType.APPLICATION_JSON);
        }};
    }

    protected  <T> HttpEntity<T> createHttpEntity(T body, String bearer)
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

    protected ResponseEntity<String> executeExchange(String path, String bearer, HttpMethod method) {
        return executeExchangeWithBody(path, bearer, method, null);
    }

    protected ResponseEntity<String> deleteUser(String bearer) {
        return executeExchange("/users", bearer, HttpMethod.DELETE);
    }

    protected  <T> T parseJson(String json, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected CardDTO sendCreateCardRequest(String bearer) {
        ResponseEntity<String> createCard = executeExchange("/cards", bearer,
                HttpMethod.PUT);
        assertEquals(HttpStatus.CREATED, createCard.getStatusCode());
        return parseJson(createCard.getBody(), CardDTO.class);
    }

    protected long getCardBalance(long cardId, String bearer) {
        ResponseEntity<String> responseGetCard = executeExchange("/cards/" + cardId,
                bearer, HttpMethod.GET);
        assertEquals(HttpStatus.OK, responseGetCard.getStatusCode());
        CardDTO cardNew = parseJson(responseGetCard.getBody(), CardDTO.class);
        return cardNew.getAmount();
    }

    protected String sendTransferRequest(int cardIdFrom, int cardIdTo, long amount,
                                         HttpStatus expected, String bearer) {
        RequestTransferDTO requestTransferDTO = new RequestTransferDTO(cardIdTo, amount);
        ResponseEntity<String> responseTransfer = executeExchangeWithBody("/transfer/" + cardIdFrom,
                bearer, HttpMethod.PUT, requestTransferDTO);
        assertEquals(expected, responseTransfer.getStatusCode());
        return responseTransfer.getBody();
    }

    protected String sendCompleteTransfer(String token, HttpStatus status, String bearer) {
        CompleteTransferDTO completeTransferDTO = new CompleteTransferDTO(token);
        ResponseEntity<String> responseComplete = executeExchangeWithBody("/transfer",
                bearer, HttpMethod.PUT, completeTransferDTO);
        assertEquals(status, responseComplete.getStatusCode());
        return responseComplete.getBody();
    }

    protected String sendDeposit(long cardId, long amount, String bearer, HttpStatus expected) {
        DepositCardDTO depositCardDTO = new DepositCardDTO(amount);
        ResponseEntity<String> responseDeposit = executeExchangeWithBody("/cards/" + cardId,
                bearer, HttpMethod.POST, depositCardDTO);
        assertEquals(expected, responseDeposit.getStatusCode());
        return responseDeposit.getBody();
    }

    protected CardDTO createTransfer(int cardIdFrom, int cardIdTo, long amount, String bearer) {
        String s = sendTransferRequest(cardIdFrom, cardIdTo, amount,
                HttpStatus.OK, bearer);
        VerifyTransferDTO verifyTransferDTO = parseJson(s, VerifyTransferDTO.class);
        s = sendCompleteTransfer(verifyTransferDTO.getToken(), HttpStatus.OK, bearer);
        return parseJson(s, CardDTO.class);
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
}
