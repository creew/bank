package com.example.bank;

import com.example.bank.dto.request.CredentialsDTO;
import com.example.bank.dto.request.UserRegisterDTO;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAuth extends AbstractTest{

    private static final String AUTH_PATH = "/auth";

    private static final String AUTH_PATH_SIGNUP = AUTH_PATH + "/signup";

    private static final Logger logger = LoggerFactory.getLogger(TestAuth.class);

    @Test
    void testSignIn() {
        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(getContextPath() +
                        AUTH_PATH + "/signin",
                new CredentialsDTO(registeredUser.login, registeredUser.password), JsonNode.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testSignInFail() {
        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(getContextPath() +
                        AUTH_PATH + "/signin",
                new CredentialsDTO("user", "password"), JsonNode.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Nested
    class WithoutBeforeAfterEach {

        @Test
        void createUserWithNullValues() {
            UserRegisterDTO userRegisterDTO = new UserRegisterDTO("11", "22", "22", null, "22", "22");
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(getContextPath() + AUTH_PATH_SIGNUP,
                    userRegisterDTO, String.class);
            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        }

        @Test
        void createUserDuplicate() {
            String bearer1 = null;
            try {
                UserRegisterDTO userRegisterDTO = new UserRegisterDTO("11", "22", "22", "22", "22", "22");
                ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(getContextPath() +
                        AUTH_PATH_SIGNUP, userRegisterDTO, JsonNode.class);
                assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
                bearer1 = Objects.requireNonNull(responseEntity.getBody()).get("bearer").asText();
                ResponseEntity<JsonNode> responseEntity2 = restTemplate.postForEntity(getContextPath() +
                        AUTH_PATH_SIGNUP, userRegisterDTO, JsonNode.class);
                logger.info("body {}", responseEntity2.getBody().toString());
                assertEquals(HttpStatus.CONFLICT, responseEntity2.getStatusCode());
            } finally {
                if (bearer1 != null)
                    deleteUser(bearer1);
            }
        }

    }
}
