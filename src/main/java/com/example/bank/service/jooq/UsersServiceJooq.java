package com.example.bank.service.jooq;

import com.example.bank.dto.UserDTO;
import com.example.bank.dto.request.UserRegisterDTO;
import com.example.bank.dto.response.AuthenticatedUserTokenDTO;
import com.example.bank.exception.DuplicateEntryException;
import com.example.bank.exception.IllegalArgumentsPassed;
import com.example.bank.exception.WrongPasswordException;
import com.example.bank.jooq.tables.records.UsersRecord;
import com.example.bank.service.UsersService;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import static com.example.bank.jooq.tables.AuthorizationToken.AUTHORIZATION_TOKEN;
import static com.example.bank.jooq.tables.Users.USERS;

@Service
public class UsersServiceJooq implements UsersService {

    private final DSLContext dslContext;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UsersServiceJooq(DSLContext dslContext, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.dslContext = dslContext;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    private static class UserDTOMapper implements RecordMapper<UsersRecord, UserDTO> {
        @Override
        public UserDTO map(UsersRecord record) {
            return new UserDTO(record.getId());
        }
    }

    public LocalDateTime convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @Transactional
    public AuthenticatedUserTokenDTO saveTransaction(int userId, long expiredInSeconds) {
        String tokenValue = UUID.randomUUID().toString();
        Date curDate = new Date();
        int res = dslContext.insertInto(AUTHORIZATION_TOKEN)
                .columns(AUTHORIZATION_TOKEN.FK_USER_ID, AUTHORIZATION_TOKEN.TIME_CREATED,
                        AUTHORIZATION_TOKEN.TIME_EXPIRATION, AUTHORIZATION_TOKEN.TOKEN)
                .values(userId, convertToLocalDateViaInstant(curDate),
                        convertToLocalDateViaInstant(new Date(curDate.getTime() + expiredInSeconds * 1000)),
                        tokenValue)
                .execute();
        if (res != 1) {
            throw new RuntimeException();
        }
        return new AuthenticatedUserTokenDTO(tokenValue);
    }

    @Transactional
    public AuthenticatedUserTokenDTO addUser(UserRegisterDTO userRegDTO) {
        byte[] password = bCryptPasswordEncoder.encode(userRegDTO.getPassword()).getBytes();
        UsersRecord usersRecord = dslContext.insertInto(USERS)
                .columns(USERS.LOGIN, USERS.PASSWORD, USERS.FIRST_NAME, USERS.LAST_NAME, USERS.PATRONYMIC)
                .values(userRegDTO.getLogin(), password, userRegDTO.getFirstName(),
                        userRegDTO.getLastName(), userRegDTO.getPatronymic())
                .returning(USERS.ID)
                .fetchOne();
        return saveTransaction(usersRecord.getId(), 60L * 60 * 24);
    }

    @Override
    public AuthenticatedUserTokenDTO createUser(UserRegisterDTO request) {
        if (dslContext.selectFrom(USERS)
                .where(USERS.LOGIN.eq(request.getLogin()))
                .execute() != 0) {
            throw new DuplicateEntryException("User: " + request.getLogin() + " already exists");
        }
        return addUser(request);
    }

    @Override
    public AuthenticatedUserTokenDTO loginUser(String username, String password) {
        UsersRecord record = dslContext.selectFrom(USERS)
                .where(USERS.LOGIN.eq(username))
                .fetchOne();
        if (record == null) {
            throw new IllegalArgumentsPassed("User not found");
        }
        if (!bCryptPasswordEncoder.matches(password, new String(record.getPassword()))) {
            throw new WrongPasswordException("Incorrect password");
        }
        return saveTransaction(record.getId(), 60L * 60 * 24);
    }

    @Override
    public UserDTO findUserByLogin(String login) {
        return dslContext.selectFrom(USERS)
                .where(USERS.LOGIN.eq(login))
                .fetchOne(new UserDTOMapper());
    }

    @Override
    public void deleteUserById(int id) {
        dslContext.deleteFrom(USERS)
                .where(USERS.ID.eq(id))
                .execute();
    }
}
