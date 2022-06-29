package io.blog.springblogapp.repository;

import io.blog.springblogapp.model.entity.AuthorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthorityRepository extends JpaRepository<AuthorityEntity, UUID> {

    Optional<AuthorityEntity> findByName(String name);

}
