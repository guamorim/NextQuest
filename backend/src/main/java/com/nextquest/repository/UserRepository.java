package com.nextquest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nextquest.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}