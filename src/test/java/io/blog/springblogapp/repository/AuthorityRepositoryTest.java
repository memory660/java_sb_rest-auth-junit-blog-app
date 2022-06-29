package io.blog.springblogapp.repository;

import io.blog.springblogapp.model.entity.AuthorityEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuthorityRepositoryTest {

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    TestEntityManager entityManager;

    AuthorityEntity authority;

    @BeforeEach
    void setUp() {
        authority = AuthorityEntity.builder().build();
    }

    @Test
    void test_find_by_authority_name() {
        //given
        authority.setName("TEST_NEW_AUTH");
        entityManager.persist(authority);

        //act
        Optional<AuthorityEntity> result = authorityRepository.findByName("TEST_NEW_AUTH");

        //assert
        assertThat(result).isNotNull();
        assertThat(result.isPresent()).isTrue();
    }
}
