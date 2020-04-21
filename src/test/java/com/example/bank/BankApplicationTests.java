package com.example.bank;

import com.example.bank.dto.CredentialsDto;
import com.example.bank.dto.UserRegisterDto;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BankApplicationTests {

    private static final String BASE_URL = "http://localhost:";

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    HttpHeaders createHeaders(String bearer){
        return new HttpHeaders() {{
            String authHeader = "Bearer: " + bearer;
            set( "Authorization", authHeader );
        }};
    }

    @Test
    void testRegister() {
        int login = (int)(Math.random() * 1000000000);
        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(BASE_URL + port + "/api/auth/signup",
                new UserRegisterDto(Integer.toString(login), "1", "12", "12", "12", "1"), JsonNode.class);
        String bearer = responseEntity.getBody().get("bearer").asText();
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        HttpHeaders httpHeaders = createHeaders(bearer);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        ResponseEntity<String> responseEntity1 = restTemplate.exchange(BASE_URL + port + "/api/users",
                HttpMethod.DELETE,
                httpEntity,
                String.class);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity1.getStatusCode());
    }

    @Test
    void testSignin() {
        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(BASE_URL + port + "/auth/signin",
                new CredentialsDto("22", "22"), JsonNode.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        System.out.println(responseEntity.getBody());
    }

    @Test
    void testGet() {

    }

    @Test
    void contextLoads() {
    }

}
