package com.example.bank.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name= "users")
public class User implements Serializable {

    private static final long serialVersionUID = 2765105000222936867L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long userId;

    @Column(name = "UUID", length=36)
    private String uuid;

    @Column(name = "LOGIN", nullable = false)
    private String login;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "PATRONYMIC", nullable = false)
    private String patronymic;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    private AuthorizationToken authorizationToken;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public AuthorizationToken getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(AuthorizationToken authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public User(UUID uuid) {
        this.uuid = uuid.toString();
    }

    public User() {
        this(UUID.randomUUID());
    }

    public UUID getUuid() {
        return UUID.fromString(uuid);
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId.equals(user.userId) &&
                uuid.equals(user.uuid) &&
                login.equals(user.login) &&
                password.equals(user.password) &&
                firstName.equals(user.firstName) &&
                lastName.equals(user.lastName) &&
                patronymic.equals(user.patronymic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, uuid, login, password, firstName, lastName, patronymic);
    }

    public String getPrincipal() {
        return firstName + " " + patronymic + " " + lastName.substring(0, 1);
    }
}
