package com.example.bank.dto;

public class AuthenticatedUserTokenDto {

    private String userId;

    private String token;

    public AuthenticatedUserTokenDto() {
    }

    public AuthenticatedUserTokenDto(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return userId + "." + token;
    }
}
