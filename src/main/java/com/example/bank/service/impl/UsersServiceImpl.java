package com.example.bank.service.impl;

import com.example.bank.dao.UserRepository;
import com.example.bank.dto.UserDTO;
import com.example.bank.dto.request.UserRegisterDTO;
import com.example.bank.dto.response.AuthenticatedUserTokenDTO;
import com.example.bank.entity.AuthorizationToken;
import com.example.bank.entity.User;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.exception.WrongPasswordException;
import com.example.bank.service.UsersService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsersServiceImpl implements UsersService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UsersServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentsPassed("No customer with id " + id + " found"));
    }

    private User createNewUser(UserRegisterDTO customer) {
        User newUser = new User();
        newUser.setLogin(customer.getLogin());
        newUser.setFirstName(customer.getFirstName());
        newUser.setLastName(customer.getLastName());
        newUser.setPatronymic(customer.getPatronymic());
        newUser.setPassword(bCryptPasswordEncoder.encode(customer.getPassword()).getBytes());
        return newUser;
    }

    @Transactional
    public AuthenticatedUserTokenDTO createAuthorizationToken(User user) {
        AuthorizationToken token = user.getAuthorizationToken();
        if (token == null || token.hasExpired()) {
            token = new AuthorizationToken(user);
            user.setAuthorizationToken(token);
            token = userRepository.saveAndFlush(user).getAuthorizationToken();
        }
        return AuthenticatedUserTokenDTO.fromAuthorizationToken(token);
    }

    @Override
    @Transactional
    public AuthenticatedUserTokenDTO createUser(UserRegisterDTO request) {
        User saved = userRepository.save(createNewUser(request));
        return createAuthorizationToken(saved);
    }

    @Override
    @Transactional
    public AuthenticatedUserTokenDTO loginUser(String username, String password) {
        User searchedForUser = userRepository.findUserByLogin(username);
        if (searchedForUser == null) {
            throw new IllegalArgumentsPassed("User not found");
        }
        if (!bCryptPasswordEncoder.matches(password, new String(searchedForUser.getPassword()))) {
            throw new WrongPasswordException("Incorrect password");
        }
        return createAuthorizationToken(searchedForUser);
    }

    @Override
    public UserDTO findUserByLogin(String login) {
        User user = userRepository.findUserByLogin(login);
        if (user == null)
            return null;
        return UserDTO.fromUser(user);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
