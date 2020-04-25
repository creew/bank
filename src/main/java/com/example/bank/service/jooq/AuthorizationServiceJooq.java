package com.example.bank.service.jooq;

import com.example.bank.dto.UserDTO;
import com.example.bank.service.AuthorizationService;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.bank.jooq.tables.AuthorizationToken.AUTHORIZATION_TOKEN;

@Service
public class AuthorizationServiceJooq implements AuthorizationService {

    private Logger logger = LoggerFactory.getLogger(AuthorizationServiceJooq.class);

    private  DSLContext dslContext;

    public AuthorizationServiceJooq(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public void deleteAuthorizationToken(String token) {
        dslContext.deleteFrom(AUTHORIZATION_TOKEN)
                .where(AUTHORIZATION_TOKEN.TOKEN.eq(token));
    }

    @Override
    public Optional<UserDTO> getUserFromAuthorizationToken(String token) {
        UserDTO userDTO = dslContext.selectFrom(AUTHORIZATION_TOKEN)
                .where(AUTHORIZATION_TOKEN.TOKEN.eq(token)).fetchOne(mapper -> new UserDTO(mapper.getId()));
        logger.info("getUserFromAuthorizationToken: token: " + token);
        return Optional.ofNullable(userDTO);
    }
}
