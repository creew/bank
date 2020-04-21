package com.example.bank.service;

import com.example.bank.dto.AuthenticatedUserTokenDto;
import com.example.bank.entity.AuthorizationToken;
import com.example.bank.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UUIDAuthenticationService implements UserAuthenticationService {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorizationService authorizationService;

    @Override
    public Optional<String> login(String username, String password) {
        AuthenticatedUserTokenDto tokenDto = userService.loginUser(username, password);
        return Optional.of(tokenDto.toString());
    }

    @Override
    public Optional<User> findByNameToken(String userId, String token) {
        User user = userService.findUserByUuid(userId);
        if (user != null) {
            AuthorizationToken authorizationToken = user.getAuthorizationToken();
            if (authorizationToken != null
                    && !authorizationToken.hasExpired()
                    && authorizationToken.getToken().equals(token)
            ) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public void logout(User user) {
        AuthorizationToken authorizationToken = user.getAuthorizationToken();
        if (authorizationToken != null) {
            authorizationService.deleteAuthorizationToken(authorizationToken.getId());
        }
    }
}
