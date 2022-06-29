package io.blog.springblogapp.service;

import io.blog.springblogapp.model.entity.UserEntity;
import io.blog.springblogapp.repository.UserRepository;
import io.blog.springblogapp.service.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserDetailsServiceImplTest {

    private static final String ID = "0d7403bc-0ba6-47c9-9499-11aebfff6969";
    private static final String PUBLIC_USER_ID = "AilzjCjfKFXq9PQSHMFxa6EXG6cC6cdKLfC6d7Cb";
    private static final String FIRST_NAME = "Éric";
    private static final String LAST_NAME = "Magalhães";
    private static final String EMAIL = "test@test.com";
    private static final String ENCRYPTED_PASSWORD = "$2a$10$RToYv0ZYYBVVjdL6oftLheXszKlAef/WzhsD2It3eWY44XkASpXVu"; //123

    @SpyBean
    UserDetailsServiceImpl userDetailsService;
    @MockBean
    UserRepository userRepository;

    UserEntity user;

    @BeforeEach
    void setUp() {
        user = UserEntity.builder()
                .id(UUID.fromString(ID)).userId(PUBLIC_USER_ID).firstName(FIRST_NAME).lastName(LAST_NAME).email(EMAIL).encryptedPassword(ENCRYPTED_PASSWORD).build();
    }

    @Test
    void test_load_user_by_username() {
        //given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        user.setEmailVerificationStatus(true);

        //act
        userDetailsService.loadUserByUsername(EMAIL);

        //assert
        verify(userRepository, times(1)).findByEmail(EMAIL);
    }

    @Test
    void test_throw_exception_when_try_to_find_user() {
        //given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        //act & assert
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(EMAIL));
        verify(userRepository, times(1)).findByEmail(EMAIL);
    }
}
