package com.example.bank;

import com.example.bank.dto.CardDTO;
import com.example.bank.dto.DepositCardDTO;
import com.example.bank.dto.CredentialsDTO;
import com.example.bank.dto.UserRegisterDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BankApplicationTests {

    private static final String BASE_URL = "http://localhost:";

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @Value("${server.servlet.context-path}")
    private String servletContextPath;

    @Autowired
    public void setRestTemplate(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    private String getContextPath() {
        return BASE_URL + port + servletContextPath;
    }

    private RegisterUser registeredUser;

    @BeforeEach
    void beforeEach() {
        registeredUser = registerUser();
        assertEquals(HttpStatus.CREATED, registeredUser.responseEntity.getStatusCode());
    }

    @AfterEach
    void afterEach() {
        ResponseEntity<String> responseEntity= deleteUser(registeredUser.bearer);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }



    static class RegisterUser {
        String login;
        String password;
        String bearer;
        ResponseEntity<JsonNode> responseEntity;

        public RegisterUser() {
            this.login = Integer.toString((int)(Math.random() * 1_000_000_000));
            this.password = Integer.toString((int)(Math.random() * 1_000_000_000));
        }
    }

    private RegisterUser registerUser() {
        RegisterUser registerUser = new RegisterUser();
        registerUser.responseEntity = restTemplate.postForEntity(getContextPath() + "/auth/signup",
                new UserRegisterDTO(registerUser.login, registerUser.password, "12", "12", "12", registerUser.password), JsonNode.class);
        registerUser.bearer = registerUser.responseEntity.getBody().get("bearer").asText();
        return registerUser;
    }

    static HttpHeaders createHeaders(String bearer){
        return new HttpHeaders() {{
            setBearerAuth(bearer);
            setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            setContentType(MediaType.APPLICATION_JSON);
        }};
    }

    private <TI> HttpEntity<TI> createHttpEntity(TI body, String bearer)
    {
        HttpHeaders httpHeaders = createHeaders(bearer);
        return new HttpEntity<>(body, httpHeaders);
    }

    private <TO, TI> ResponseEntity<TO> executeExchangeWithBody(String path, String bearer, HttpMethod method,
                                                                Class<TO> clazz, TI body) {
        HttpEntity<TI> httpEntity = createHttpEntity(body, bearer);
        return restTemplate.exchange(getContextPath() + path,
                method,
                httpEntity,
                clazz);
    }

    private <T> ResponseEntity<T> executeExchange(String path, String bearer, HttpMethod method, Class<T> clazz) {
        return executeExchangeWithBody(path, bearer, method, clazz, null);
    }

    private ResponseEntity<String> deleteUser(String bearer) {
        return executeExchange("/users", bearer, HttpMethod.DELETE, String.class);
    }

    private <T> T parseJson(String json, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSignIn() {
        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(getContextPath() + "/auth/signin",
                new CredentialsDTO(registeredUser.login, registeredUser.password), JsonNode.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testGetAllCardsEmpty() {
        ResponseEntity<Object[]> responseEntity = executeExchange("/cards", registeredUser.bearer,
                HttpMethod.GET, Object[].class);
        System.out.println(Arrays.toString(responseEntity.getBody()));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testGetAllCardsNotEmpty() {
        ResponseEntity<Object[]> responseEntity = executeExchange("/cards", registeredUser.bearer,
                HttpMethod.GET, Object[].class);
        System.out.println(Arrays.toString(responseEntity.getBody()));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testCreateNewCard() {
        ResponseEntity<String> responseEntity = executeExchange("/cards", registeredUser.bearer,
                HttpMethod.PUT, String.class);
        System.out.println(responseEntity.getBody());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        CardDTO cardDTO = parseJson(responseEntity.getBody(), CardDTO.class);
        assertEquals(0, cardDTO.getAmount());
    }

    @Test
    void testCreateDeposit() {
        ResponseEntity<String> responseEntity = executeExchange("/cards", registeredUser.bearer,
                HttpMethod.PUT, String.class);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        CardDTO cardDTO = parseJson(responseEntity.getBody(), CardDTO.class);

        DepositCardDTO depositCardDTO = new DepositCardDTO(1234);
        ResponseEntity<String> responseDeposit = executeExchangeWithBody("/cards/" + cardDTO.getCardId(),
                registeredUser.bearer, HttpMethod.POST, String.class, depositCardDTO);
        assertEquals(HttpStatus.OK, responseDeposit.getStatusCode());

        CardDTO updatedCard = parseJson(responseDeposit.getBody(), CardDTO.class);
        assertEquals(1234, updatedCard.getAmount());
    }
}
