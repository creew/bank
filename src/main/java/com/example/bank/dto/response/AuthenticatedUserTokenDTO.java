package com.example.bank.dto.response;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class AuthenticatedUserTokenDTO implements Serializable {

    private static final long serialVersionUID = 1954787934193588147L;

    @NotNull
    private String token;

    public AuthenticatedUserTokenDTO() {
    }

    public AuthenticatedUserTokenDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
