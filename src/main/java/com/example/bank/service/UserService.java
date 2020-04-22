package com.example.bank.service;

import com.example.bank.dao.UserRepository;
import com.example.bank.dto.AuthenticatedUserTokenDTO;
import com.example.bank.dto.UserRegisterDTO;
import com.example.bank.entity.AuthorizationToken;
import com.example.bank.entity.User;
import com.example.bank.exception.DuplicateEntryException;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.exception.WrongPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentsPassed("No customer with id " + id + " found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findUserByLogin(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public User createNewCustomer(UserRegisterDTO customer) {
        User newUser = new User();
        newUser.setLogin(customer.getLogin());
        newUser.setFirstName(customer.getFirstName());
        newUser.setLastName(customer.getLastName());
        newUser.setPatronymic(customer.getPatronymic());
        newUser.setPassword(bCryptPasswordEncoder.encode(customer.getPassword()));
        return newUser;
    }

    @Transactional
    public AuthorizationToken createAuthorizationToken(User user) {
        AuthorizationToken token = user.getAuthorizationToken();
        if (token == null || user.getAuthorizationToken().hasExpired()) {
            token = new AuthorizationToken(user);
            user.setAuthorizationToken(token);
            userRepository.save(user);
        }
        return token;
    }

    @Transactional
    public AuthenticatedUserTokenDTO createUser(UserRegisterDTO request) {
        User searchedForUser = userRepository.findUserByLogin(request.getLogin());
        if (searchedForUser != null) {
            throw new DuplicateEntryException("User: " + searchedForUser.getLogin() + " already exists");
        }
        User saved = userRepository.save(createNewCustomer(request));
        return new AuthenticatedUserTokenDTO(saved.getUuid().toString(), createAuthorizationToken(saved).getToken());
    }

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
        return new AuthenticatedUserTokenDTO(searchedForUser.getUuid().toString(),
                createAuthorizationToken(searchedForUser).getToken());
    }

    public User findUserByUuid(String uuid) {
        return userRepository.findUserByUuid(uuid);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
