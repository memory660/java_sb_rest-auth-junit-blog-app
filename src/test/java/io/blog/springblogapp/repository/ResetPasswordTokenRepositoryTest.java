package io.blog.springblogapp.repository;

import io.blog.springblogapp.model.entity.ResetPasswordToken;
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
public class ResetPasswordTokenRepositoryTest {

    private static final String JWT_TOKEN_VALID = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGVtYWlsLmNvbSIsInVzZXJJZCI6IjJEY0o4YUdDTTVkRVJzMU1oYlFjc3Z5MEtoTHFhMTk5RDE1b0habEsiLCJleHAiOjMyMzE4MzAzNDF9.phSTqexKpKdqiHBLqfnDd2gPg5kWWH4VmLxkykm73dCsk4cMcmk80mePlpsRyYnX8XyXazpIH0hBFpEl67r-Iw";

    @Autowired
    ResetPasswordTokenRepository resetPasswordTokenRepository;

    @Autowired
    TestEntityManager entityManager;

    ResetPasswordToken resetPasswordToken;

    @BeforeEach
    void setUp() {
        resetPasswordToken = ResetPasswordToken.builder().token(JWT_TOKEN_VALID).build();
    }

    @Test
    void test_find_by_token() {
        //given
        entityManager.persist(resetPasswordToken);

        //act
        Optional<ResetPasswordToken> result = resetPasswordTokenRepository.findByToken(JWT_TOKEN_VALID);

        //assert
        assertThat(result).isNotNull();
        assertThat(result.isPresent()).isTrue();
    }
}
