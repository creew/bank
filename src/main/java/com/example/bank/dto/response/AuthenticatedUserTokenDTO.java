package com.example.bank.dto.response;

import com.example.bank.entity.AuthorizationToken;

import java.io.Serializable;

public class AuthenticatedUserTokenDTO implements Serializable {

    private static final long serialVersionUID = 1954787934193588147L;

    private String token;

    public AuthenticatedUserTokenDTO() {
    }

    public AuthenticatedUserTokenDTO(String token) {
        this.token = token;
    }

    public static AuthenticatedUserTokenDTO fromAuthorizationToken(AuthorizationToken token) {
        AuthenticatedUserTokenDTO authenticatedUserTokenDTO = new AuthenticatedUserTokenDTO();
        authenticatedUserTokenDTO.token = token.getId().toString();
        return authenticatedUserTokenDTO;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
