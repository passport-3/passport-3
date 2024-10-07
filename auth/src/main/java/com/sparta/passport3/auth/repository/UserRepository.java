package com.sparta.passport3.auth.repository;

import com.sparta.passport3.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository <User, Long>{

    Optional<User> findByUsername(String username);
}
