package com.example.bank.service;

import com.example.bank.dao.UserRepository;
import com.example.bank.dto.AuthenticatedUserTokenDto;
import com.example.bank.dto.UserRegisterDto;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByLogin(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public User createNewCustomer(UserRegisterDto customer) {
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
        if(user.getAuthorizationToken() == null || user.getAuthorizationToken().hasExpired()) {
            user.setAuthorizationToken(new AuthorizationToken(user));
            userRepository.save(user);
        }
        return user.getAuthorizationToken();
    }

    @Transactional
    public AuthenticatedUserTokenDto createUser(UserRegisterDto request) {
        User searchedForUser = userRepository.findUserByLogin(request.getLogin());
        if (searchedForUser != null) {
            throw new DuplicateEntryException("User: " + searchedForUser.getLogin() + " already exists");
        }
        User saved = userRepository.save(createNewCustomer(request));
        return new AuthenticatedUserTokenDto(saved.getUuid().toString(), createAuthorizationToken(saved).getToken());
    }

    @Transactional
    public AuthenticatedUserTokenDto loginUser(String username, String password) {
        User searchedForUser = userRepository.findUserByLogin(username);
        if (searchedForUser == null) {
            throw new IllegalArgumentsPassed("User not found");
        }
        String cryptedPassword = bCryptPasswordEncoder.encode(password);
        if (!bCryptPasswordEncoder.matches(password, cryptedPassword)) {
            throw new WrongPasswordException("Incorrect password");
        }
        return new AuthenticatedUserTokenDto(searchedForUser.getUuid().toString(),
                createAuthorizationToken(searchedForUser).getToken());
    }

    public User findUserByUuid(String uuid) {
        return userRepository.findUserByUuid(uuid);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
