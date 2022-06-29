package io.blog.springblogapp.repository;

import io.blog.springblogapp.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUserId(String userId);

    Optional<UserEntity> findUserByEmailVerificationToken(String token);

}
