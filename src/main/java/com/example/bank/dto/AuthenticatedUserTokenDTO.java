package com.example.bank.dto;

import java.io.Serializable;

public class AuthenticatedUserTokenDTO implements Serializable {

    private static final long serialVersionUID = 1954787934193588147L;

    private String userId;

    private String token;

    public AuthenticatedUserTokenDTO() {
    }

    public AuthenticatedUserTokenDTO(String userId, String token) {
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
