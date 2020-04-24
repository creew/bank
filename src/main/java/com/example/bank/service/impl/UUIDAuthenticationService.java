package com.example.bank.service.impl;

import com.example.bank.dto.UserDTO;
import com.example.bank.dto.response.AuthenticatedUserTokenDTO;
import com.example.bank.entity.AuthorizationToken;
import com.example.bank.entity.User;
import com.example.bank.service.AuthorizationService;
import com.example.bank.service.UserAuthenticationService;
import com.example.bank.service.UsersService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UUIDAuthenticationService implements UserAuthenticationService {

    private final UsersService usersService;

    private final AuthorizationService authorizationService;

    public UUIDAuthenticationService(UsersService usersService, AuthorizationService authorizationService) {
        this.usersService = usersService;
        this.authorizationService = authorizationService;
    }

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
    public void logout(UserDTO userDTO) {
        User user = usersService.getUserById(userDTO.getId());
        AuthorizationToken authorizationToken = user.getAuthorizationToken();
        if (authorizationToken != null) {
            authorizationService.deleteAuthorizationToken(authorizationToken.getId());
        }
    }
}
