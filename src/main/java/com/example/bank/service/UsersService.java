package com.example.bank.service;

import com.example.bank.dto.CardDTO;
import com.example.bank.dto.UserDTO;
import com.example.bank.dto.request.UserRegisterDTO;
import com.example.bank.dto.response.AuthenticatedUserTokenDTO;
import com.example.bank.entity.User;
import org.springframework.transaction.annotation.Transactional;

public interface UsersService {

    AuthenticatedUserTokenDTO createUser(UserRegisterDTO request);

    AuthenticatedUserTokenDTO loginUser(String username, String password);

    UserDTO findUserByLogin(String login);

    void deleteUserById(int id);

}
