package io.blog.springblogapp.service;

import io.blog.springblogapp.dto.UserDto;
import io.blog.springblogapp.service.impl.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class JwtServiceImplTest {

    private static final String ID = "0d7403bc-0ba6-47c9-9499-11aebfff6969";
    private static final String PUBLIC_USER_ID = "AilzjCjfKFXq9PQSHMFxa6EXG6cC6cdKLfC6d7Cb";
    private static final String FIRST_NAME = "Éric";
    private static final String LAST_NAME = "Magalhães";
    private static final String EMAIL = "test@email.com";
    private static final String ENCRYPTED_PASSWORD = "$2a$10$RToYv0ZYYBVVjdL6oftLheXszKlAef/WzhsD2It3eWY44XkASpXVu"; //123
    //expire 2072
    private static final String JWT_TOKEN_VALID = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGVtYWlsLmNvbSIsInVzZXJJZCI6IjJEY0o4YUdDTTVkRVJzMU1oYlFjc3Z5MEtoTHFhMTk5RDE1b0habEsiLCJleHAiOjMyMzE4MzAzNDF9.phSTqexKpKdqiHBLqfnDd2gPg5kWWH4VmLxkykm73dCsk4cMcmk80mePlpsRyYnX8XyXazpIH0hBFpEl67r-Iw";
    private static final String JWT_TOKEN_EXPIRED = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGVtYWlsLmNvbSIsInVzZXJJZCI6IjJEY0o4YUdDTTVkRVJzMU1oYlFjc3Z5MEtoTHFhMTk5RDE1b0habEsiLCJleHAiOjE2NTM5NTA1MjF9.vlJveC1Gv7EdXblgPVixKlpf7FHOiPD5RLLeUm9SYIrbiGyDa3N6kDq6gEiMkElVVVI4Fg9qkha1bgrlVJauYA";

    @Autowired
    JwtServiceImpl jwtService;

    UserDto user;


    @BeforeEach
    void setUp() {
        user = UserDto.builder().id(UUID.fromString(ID)).userId(PUBLIC_USER_ID).firstName(FIRST_NAME).lastName(LAST_NAME)
                .email(EMAIL).encryptedPassword(ENCRYPTED_PASSWORD).build();
    }

    @Test
    void test_is_valid_token() {
        //act
        boolean isValid = jwtService.isValidToken(JWT_TOKEN_VALID);

        //assert
        assertThat(isValid).isTrue();
    }

    @Test
    void test_is_not_valid_token() {
        //act
        boolean isValid = jwtService.isValidToken(JWT_TOKEN_EXPIRED);

        //assert
        assertThat(isValid).isFalse();
    }

    @Test
    void test_get_claim() {
        //act
        Claims claims = jwtService.getClaims(JWT_TOKEN_VALID);

        //assert
        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo(EMAIL);
    }

    @Test
    void test_throw_exception_when_try_to_get_claim_with_invalid_token() {
        //act & assert
        assertThrows(ExpiredJwtException.class, () -> jwtService.getClaims(JWT_TOKEN_EXPIRED));
    }

    @Test
    void test_get_username_with_valid_token() {
        //act
        String username = jwtService.getUsername(JWT_TOKEN_VALID);

        //assert
        assertThat(username).isNotNull();
        assertThat(username).isEqualTo(EMAIL);
    }

    @Test
    void test_throw_exception_when_try_to_get_username_with_invalid_token() {
        //act & assert
        assertThrows(ExpiredJwtException.class, () -> jwtService.getUsername(JWT_TOKEN_EXPIRED));
    }

    @Test
    void test_generate_token() {
        //act
        String token = jwtService.generateToken(user);

        //assert
        assertThat(token).isNotNull();
    }

    @Test
    void test_generate_email_token_validation() {
        //act
        String emailTokenValidation = jwtService.generateEmailTokenValidation(user);

        //assert
        assertThat(emailTokenValidation).isNotNull();
    }

    @Test
    void test_generate_reset_password_token() {
        //act
        String resetPasswordToken = jwtService.generateResetPasswordToken(user.getUserId());

        //assert
        assertThat(resetPasswordToken).isNotNull();
    }
}
