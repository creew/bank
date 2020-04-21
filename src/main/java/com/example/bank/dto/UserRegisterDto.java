package com.example.bank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserRegisterDto {

    @JsonProperty("login")
    private String login;

    @JsonProperty("password")
    private String password;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("patronymic")
    private String patronymic;

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

    public UserRegisterDto() {
    }

    public UserRegisterDto(String login, String password, String firstName, String lastName, String patronymic, String passwordConfirm) {
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
