package com.example.bank.service.impl;

import com.example.bank.dao.UserRepository;
import com.example.bank.dto.response.AuthenticatedUserTokenDTO;
import com.example.bank.dto.request.UserRegisterDTO;
import com.example.bank.entity.AuthorizationToken;
import com.example.bank.entity.User;
import com.example.bank.exception.DuplicateEntryException;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.exception.WrongPasswordException;
import com.example.bank.service.UsersService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    @Override
    public User createNewUser(UserRegisterDTO customer) {
        User newUser = new User();
        newUser.setLogin(customer.getLogin());
        newUser.setFirstName(customer.getFirstName());
        newUser.setLastName(customer.getLastName());
        newUser.setPatronymic(customer.getPatronymic());
        newUser.setPassword(bCryptPasswordEncoder.encode(customer.getPassword()));
        return newUser;
    }

    @Override
    @Transactional
    public AuthenticatedUserTokenDTO createAuthorizationToken(User user) {
        AuthorizationToken token = user.getAuthorizationToken();
        if (token == null || user.getAuthorizationToken().hasExpired()) {
            token = new AuthorizationToken(user);
            user.setAuthorizationToken(token);
            userRepository.saveAndFlush(user);
        }
        return AuthenticatedUserTokenDTO.fromAuthorizationToken(token);
    }

    @Override
    @Transactional
    public AuthenticatedUserTokenDTO createUser(UserRegisterDTO request) {
        User searchedForUser = userRepository.findUserByLogin(request.getLogin());
        if (searchedForUser != null) {
            throw new DuplicateEntryException("User: " + searchedForUser.getLogin() + " already exists");
        }
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
        String cryptedPassword = bCryptPasswordEncoder.encode(password);
        if (!bCryptPasswordEncoder.matches(password, cryptedPassword)) {
            throw new WrongPasswordException("Incorrect password");
        }
        return createAuthorizationToken(searchedForUser);
    }

    @Override
    public Optional<User> findUserByLogin(String login) {
        return Optional.of(userRepository.findUserByLogin(login));
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
