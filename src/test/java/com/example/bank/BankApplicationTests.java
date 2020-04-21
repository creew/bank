package com.example.bank;

import com.example.bank.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

//@SpringBootTest
class BankApplicationTests {

    private static final String BASE_URL = "http://localhost:8080/api";

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    void testRegister() {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(BASE_URL + "/auth/signup",
                new UserDto("12", "12", "12", "12", "12", "12"), String.class);
        System.out.println(responseEntity.getBody());
    }

    @Test
    void testGet() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(BASE_URL + "/cards",
                String.class);
        System.out.println(responseEntity.getBody());
    }

    @Test
    void contextLoads() {
    }

}
