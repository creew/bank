package com.example.bank.dao;

import com.example.bank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByLogin(String login);

    User findUserByUuid(String uuid);

}
