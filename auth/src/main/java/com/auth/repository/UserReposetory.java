package com.auth.repository;

import com.auth.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserReposetory extends JpaRepository<UserEntity , Long> {

    Optional<UserEntity> findByEmail(String email);

    Boolean existsByEmail(String email);
}
