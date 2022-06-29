package io.blog.springblogapp.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UtilsTest {

    private static final String ID = "0d7403bc-0ba6-47c9-9499-11aebfff6969";
    private static final String PUBLIC_USER_ID = "AilzjCjfKFXq9PQSHMFxa6EXG6cC6cdKLfC6d7Cb";
    private static final String FIRST_NAME = "Éric";
    private static final String LAST_NAME = "Magalhães";
    private static final String EMAIL = "test@test.com";
    private static final String ENCRYPTED_PASSWORD = "$2a$10$RToYv0ZYYBVVjdL6oftLheXszKlAef/WzhsD2It3eWY44XkASpXVu"; //123
    private static final String JWT_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0ZUBlbWFpbC5jb20iLCJleHAiOjE2NTMwMDY3NTh9.JBPbOXQpNHRDqcax2ItLeBuxK8akpzElwdpu62UZfPNEVighXOLamHuZeGpPlLv3rtrCgoieN9tsVSDfWOGC6g";

    @Autowired
    Utils utils;

    @BeforeEach
    void setUp() {

    }

    @Test
    void test_generate_user_id() {
        //act
        String userId = utils.generateUserId();

        //assert
        assertThat(userId.length()).isEqualTo(40);
        assertThat(userId.length()).isNotNull();
    }

    @Test
    void test_generate_user_address_id() {
        //act
        String userId = utils.generateAddressId();

        //assert
        assertThat(userId.length()).isEqualTo(40);
        assertThat(userId.length()).isNotNull();
    }
}
