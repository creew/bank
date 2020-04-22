package com.example.bank.security.config;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String BEARER = "Bearer";

    public TokenAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    private String removeBearer(String str) {
        int bearerLength = BEARER.length();
        if (str.length() >= bearerLength && str.substring(0, bearerLength).equals(BEARER)) {
            return str.substring(bearerLength + 1);
        }
        return str;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String param = ofNullable(request.getHeader(AUTHORIZATION))
                .orElse(request.getParameter("t"));
        String tokenFull = ofNullable(param)
                .map(this::removeBearer)
                .map(String::trim)
                .orElseThrow(() -> new BadCredentialsException("Missing Authentication Token"));
        Authentication auth = new UsernamePasswordAuthenticationToken("", tokenFull);
        return getAuthenticationManager().authenticate(auth);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }
}
