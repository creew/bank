package com.example.bank.dao;

import com.example.bank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findUserByLogin(String login);

    User findUserByUuid(String uuid);

}
