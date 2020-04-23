package com.example.bank.service.authentication;

import com.example.bank.dto.response.AuthenticatedUserTokenDTO;
import com.example.bank.dto.UserDTO;
import com.example.bank.entity.AuthorizationToken;
import com.example.bank.entity.User;
import com.example.bank.service.AuthorizationService;
import com.example.bank.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UUIDAuthenticationService implements UserAuthenticationService {

    @Autowired
    private UsersService usersService;

    @Autowired
    private AuthorizationService authorizationService;

    @Override
    public Optional<String> login(String username, String password) {
        AuthenticatedUserTokenDTO tokenDto = usersService.loginUser(username, password);
        return Optional.of(tokenDto.getToken());
    }

    @Override
    public Optional<UserDTO> findByToken(String token) {
        return authorizationService.getUserFromAuthorizationToken(token);
    }

    @Override
    public void logout(User user) {
        AuthorizationToken authorizationToken = user.getAuthorizationToken();
        if (authorizationToken != null) {
            authorizationService.deleteAuthorizationToken(authorizationToken.getId());
        }
    }
}
