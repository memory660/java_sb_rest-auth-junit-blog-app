package io.blog.springblogapp.repository;

import io.blog.springblogapp.model.entity.UserEntity;
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
public class UserRepositoryTest {

    private static final String ID = "0d7403bc-0ba6-47c9-9499-11aebfff6969";
    private static final String PUBLIC_USER_ID = "AilzjCjfKFXq9PQSHMFxa6EXG6cC6cdKLfC6d7Cb";
    private static final String FIRST_NAME = "Éric";
    private static final String LAST_NAME = "Magalhães";
    private static final String EMAIL = "test@test.com";
    private static final String ENCRYPTED_PASSWORD = "$2a$10$RToYv0ZYYBVVjdL6oftLheXszKlAef/WzhsD2It3eWY44XkASpXVu"; //123
    private static final String JWT_TOKEN_VALID = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGVtYWlsLmNvbSIsInVzZXJJZCI6IjJEY0o4YUdDTTVkRVJzMU1oYlFjc3Z5MEtoTHFhMTk5RDE1b0habEsiLCJleHAiOjMyMzE4MzAzNDF9.phSTqexKpKdqiHBLqfnDd2gPg5kWWH4VmLxkykm73dCsk4cMcmk80mePlpsRyYnX8XyXazpIH0hBFpEl67r-Iw";


    @Autowired
    UserRepository userRepository;

    @Autowired
    TestEntityManager entityManager;

    UserEntity user;

    @BeforeEach
    void setUp() {
        user = UserEntity.builder().userId(PUBLIC_USER_ID).firstName(FIRST_NAME)
                .lastName(LAST_NAME).emailVerificationStatus(false).email(EMAIL).encryptedPassword(ENCRYPTED_PASSWORD).build();
    }

    @Test
    void test_find_by_email() {
        //given
        entityManager.persist(user);

        //act
        Optional<UserEntity> result = userRepository.findByEmail(EMAIL);

        //assert
        assertThat(result).isNotNull();
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void test_find_by_user_id() {
        //given
        entityManager.persist(user);

        //act
        Optional<UserEntity> result = userRepository.findByUserId(PUBLIC_USER_ID);

        //assert
        assertThat(result).isNotNull();
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void test_find_user_by_email_verification_token() {
        //given
        user.setEmailVerificationToken(JWT_TOKEN_VALID);
        entityManager.persist(user);

        //act
        Optional<UserEntity> result = userRepository.findUserByEmailVerificationToken(JWT_TOKEN_VALID);

        //assert
        assertThat(result).isNotNull();
        assertThat(result.isPresent()).isTrue();
    }
}
