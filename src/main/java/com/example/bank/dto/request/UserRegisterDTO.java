package com.example.bank.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class UserRegisterDTO implements Serializable {

    private static final long serialVersionUID = -5148160983426777508L;

    @NotNull
    @Size(min=2, max=50)
    @JsonProperty("login")
    private String login;

    @NotNull
    @Size(min=2, max=50)
    @JsonProperty("password")
    private String password;

    @NotNull
    @Size(min=2, max=50)
    @JsonProperty("first_name")
    private String firstName;

    @NotNull
    @Size(min=2, max=50)
    @JsonProperty("last_name")
    private String lastName;

    @NotNull
    @Size(min=2, max=50)
    @JsonProperty("patronymic")
    private String patronymic;

    @NotNull
    @Size(min=2, max=50)
    @JsonProperty("password_confirm")
    private String passwordConfirm;

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public UserRegisterDTO() {
    }

    public UserRegisterDTO(String login, String password, String firstName, String lastName, String patronymic, String passwordConfirm) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.passwordConfirm = passwordConfirm;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
