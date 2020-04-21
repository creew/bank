package com.example.bank.security.config;

import com.example.bank.entity.User;
import com.example.bank.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class TokenAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Autowired
    UserAuthenticationService auth;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) {
        // no need for additional check
    }

    @Override
    protected User retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) {
        Object principal = authentication.getPrincipal();
        Object token = authentication.getCredentials();
        return auth.findByNameToken(String.valueOf(principal), String.valueOf(token))
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user with authentication token=" + token));
    }
}
