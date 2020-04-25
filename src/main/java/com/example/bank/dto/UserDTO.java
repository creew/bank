package com.example.bank.dto;

import com.example.bank.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

public class UserDTO implements UserDetails, Serializable {

    private static final long serialVersionUID = -1518990577336335727L;

    private int id;

    private String authorizationToken;

    private String username;

    private String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public UserDTO() {
    }

    public UserDTO(int id) {
        this.id = id;
    }

    public UserDTO(int id, String authorizationToken) {
        this.id = id;
        this.authorizationToken = authorizationToken;
    }

    public static UserDTO fromUser(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.id = user.getUserId();
        return userDTO;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
