package com.example.bank.service;

import com.example.bank.dto.request.UserRegisterDTO;
import com.example.bank.dto.response.AuthenticatedUserTokenDTO;
import com.example.bank.entity.User;

import java.util.Optional;

public interface UsersService {

    User getUserById(Long id);

    User createNewUser(UserRegisterDTO customer);

    AuthenticatedUserTokenDTO createAuthorizationToken(User user);

    AuthenticatedUserTokenDTO createUser(UserRegisterDTO request);

    AuthenticatedUserTokenDTO loginUser(String username, String password);

    Optional<User> findUserByLogin(String login);

    void deleteUserById(Long id);
}
