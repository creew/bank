package com.example.bank.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class CredentialsDTO implements Serializable {

    private static final long serialVersionUID = 5423052010016531011L;

    @NotNull
    @Size(min=2, max=50)
    private String username;

    @NotNull
    @Size(min=2, max=50)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CredentialsDTO() {
    }

    public CredentialsDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
