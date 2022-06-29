package io.blog.springblogapp.service;

import io.blog.springblogapp.dto.AddressDto;
import io.blog.springblogapp.dto.UserDto;
import io.blog.springblogapp.exception.AddressNotFoundException;
import io.blog.springblogapp.exception.AuthException;
import io.blog.springblogapp.exception.BusinessException;
import io.blog.springblogapp.exception.UserNotFoundException;
import io.blog.springblogapp.model.entity.AddressEntity;
import io.blog.springblogapp.model.entity.ResetPasswordToken;
import io.blog.springblogapp.model.entity.UserEntity;
import io.blog.springblogapp.model.request.ResetPasswordUpdateRequest;
import io.blog.springblogapp.repository.AddressRepository;
import io.blog.springblogapp.repository.ResetPasswordTokenRepository;
import io.blog.springblogapp.repository.RoleRepository;
import io.blog.springblogapp.repository.UserRepository;
import io.blog.springblogapp.service.impl.UserServiceImpl;
import io.blog.springblogapp.utils.ErrorMessages;
import io.blog.springblogapp.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserServiceImplTest {

    private static final String ID = "0d7403bc-0ba6-47c9-9499-11aebfff6969";
    private static final String PUBLIC_USER_ID = "AilzjCjfKFXq9PQSHMFxa6EXG6cC6cdKLfC6d7Cb";
    private static final String FIRST_NAME = "Éric";
    private static final String LAST_NAME = "Magalhães";
    private static final String EMAIL = "test@test.com";
    private static final String ENCRYPTED_PASSWORD = "$2a$10$RToYv0ZYYBVVjdL6oftLheXszKlAef/WzhsD2It3eWY44XkASpXVu"; //123
    private static final String ENCRYPTED_PASSWORD2 = "$2a$10$O0tZkQdXNdq4dChlRE3NZ.qlPQ8mQcJhNduYaEotuHDkutAvThJ0m"; //123
    private static final String JWT_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0ZUBlbWFpbC5jb20iLCJleHAiOjE2NTMwMDY3NTh9.JBPbOXQpNHRDqcax2ItLeBuxK8akpzElwdpu62UZfPNEVighXOLamHuZeGpPlLv3rtrCgoieN9tsVSDfWOGC6g";
    private static final String PUBLIC_ADDRESS_ID = "W6FgnuAqy2EhptMv10aZBE6h8cnnILZLJM0rRDWy";
    private static final String ADDRESS_ID = "1c7864ba-f205-4c37-9bb6-5a99fc39f658";
    private static final String CITY = "Vancouver";
    private static final String COUNTRY = "Canada";
    private static final String POSTAL_CODE = "A85RP3W8";
    private static final String STREET_NAME = "138 Avenue Lost";
    private static final String TYPE = "BILLING";

    @SpyBean
    UserServiceImpl userService;
    @MockBean
    PasswordEncoder passwordEncoder;
    @MockBean
    Utils utils;
    @MockBean
    UserRepository userRepository;
    @MockBean
    AddressRepository addressRepository;
    @MockBean
    ResetPasswordTokenRepository resetPasswordTokenRepository;
    @MockBean
    JwtService jwtService;
    @MockBean
    ModelMapper modelMapper;
    @MockBean
    RoleRepository roleRepository;

    UserEntity user;
    UserDto userDto;
    AddressEntity address;
    AddressDto addressDto;
    ResetPasswordToken resetPasswordToken;
    ResetPasswordUpdateRequest resetPasswordUpdateRequest;

    @BeforeEach
    void setUp() {
        user = UserEntity.builder()
                .id(UUID.fromString(ID)).userId(PUBLIC_USER_ID).firstName(FIRST_NAME).lastName(LAST_NAME).email(EMAIL).encryptedPassword(ENCRYPTED_PASSWORD).build();

        userDto = UserDto.builder()
                .id(UUID.fromString(ID)).userId(PUBLIC_USER_ID).firstName(FIRST_NAME).lastName(LAST_NAME).email(EMAIL).encryptedPassword(ENCRYPTED_PASSWORD).build();

        address = AddressEntity.builder()
                .id(UUID.fromString(ADDRESS_ID)).addressId(PUBLIC_ADDRESS_ID).city(CITY).country(COUNTRY).postalCode(POSTAL_CODE)
                .streetName(STREET_NAME).type(TYPE).user(user).build();

        addressDto = AddressDto.builder()
                .id(UUID.fromString(ADDRESS_ID)).addressId(PUBLIC_ADDRESS_ID).city(CITY).country(COUNTRY).postalCode(POSTAL_CODE)
                .streetName(STREET_NAME).type(TYPE).user(userDto).build();

        resetPasswordToken = ResetPasswordToken.builder().id(UUID.fromString(ID)).token(JWT_TOKEN).user(user).build();

        resetPasswordUpdateRequest = ResetPasswordUpdateRequest.builder().newPassword("123").confirmNewPassword("123").build();

    }

    @Test
    void test_get_exist_user_with_email() {
        //given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(modelMapper.map(any(), any())).thenReturn(userDto);

        //act
        UserDto foundUser = userService.getUser(EMAIL);

        //assert
        assertThat(foundUser).isNotNull();
        assertEquals(FIRST_NAME, userDto.getFirstName());
    }

    @Test
    void test_throw_exception_when_try_get_user_with_non_exist_email() {
        //given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        //act & assert
        Throwable exception = catchThrowable(() -> userService.getUser(EMAIL));
        assertThat(exception).isInstanceOf(UserNotFoundException.class)
                .hasMessage(ErrorMessages.NO_RECORD_FOUND_USERNAME.getErrorMessage());
    }

    @Test
    void test_create_a_new_user() {
        //given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(utils.generateUserId()).thenReturn(PUBLIC_USER_ID);
        when(utils.generateAddressId()).thenReturn(PUBLIC_ADDRESS_ID);
        when(passwordEncoder.encode(anyString())).thenReturn(ENCRYPTED_PASSWORD);
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        when(modelMapper.map(userDto, UserEntity.class)).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        List<AddressDto> addresses = new ArrayList<>();
        addresses.add(addressDto);
        userDto.setAddresses(addresses);

        //act
        UserDto createdUser = userService.createUser(userDto);

        //assert
        assertThat(createdUser).isNotNull();
        assertEquals(user.getEmail(), createdUser.getEmail());
        verify(passwordEncoder, times(1)).encode(any());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void test_throw_exception_when_try_to_create_a_new_user_with_a_used_email() {
        //given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        //act & assert
        assertThrows(AuthException.class, () -> userService.createUser(userDto));
    }

    @Test
    void test_get_user_by_public_id() {
        //given
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));
        when(modelMapper.map(any(), any())).thenReturn(userDto);

        //act
        UserDto foundedUser = userService.getUserByUserId(PUBLIC_USER_ID);

        //assert
        assertThat(foundedUser).isNotNull();
        assertEquals(PUBLIC_USER_ID, foundedUser.getUserId());
    }

    @Test
    void test_throw_exception_when_try_get_user_with_a_non_exist_user_public_id() {
        //given
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());

        //act & assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserByUserId(anyString()));
    }

    @Test
    void test_delete_exist_user() {
        //given
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));

        //act
        userService.deleteUser(PUBLIC_USER_ID);

        //assert
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void test_throw_exception_when_try_delete_that_non_exist_user() {
        //given
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());

        //act & assert
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(anyString()));

        verify(userRepository, never()).delete(user);
    }

    @Test
    void test_update_user() {
        //given
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));
        user.setFirstName("Lucas");
        user.setLastName("Pereira");
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        when(modelMapper.map(any(), any())).thenReturn(userDto);

        //act
        UserDto updatedUser = userService.updateUser(PUBLIC_USER_ID, userDto);

        //assert
        assertThat(updatedUser).isNotNull();
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void test_throw_exception_when_try_update_that_non_exist_user() {
        //given
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());

        //act & assert
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(anyString()));

        verify(userRepository, never()).save(user);
    }

    @Test
    void test_get_exist_user_address() {
        //given
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));
        when(addressRepository.findByAddressId(anyString())).thenReturn(Optional.of(address));
        when(modelMapper.map(any(), any())).thenReturn(addressDto);

        //act
        AddressDto foundAddress = userService.getUserAddress(PUBLIC_USER_ID, PUBLIC_ADDRESS_ID);

        //assert
        assertThat(foundAddress).isNotNull();
        assertEquals(user.getUserId(), foundAddress.getUser().getUserId());
    }

    @Test
    void test_throw_exception_when_try_to_get_a_user_with_non_exist_user_id() {
        //given
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());

        //act & assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserAddress(PUBLIC_USER_ID, PUBLIC_ADDRESS_ID));
    }

    @Test
    void test_throw_exception_when_try_to_get_a_user_with_non_exist_user_address_id() {
        //given
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));
        when(addressRepository.findByAddressId(anyString())).thenReturn(Optional.empty());

        //act & assert
        assertThrows(AddressNotFoundException.class, () -> userService.getUserAddress(PUBLIC_USER_ID, PUBLIC_ADDRESS_ID));
    }

    @Test
    void test_throw_exception_when_try_to_get_a_user_address_and_address_not_belong_to_the_user() {
        //given
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));
        address.setUser(null);
        when(addressRepository.findByAddressId(anyString())).thenReturn(Optional.of(address));

        //act & assert
        assertThrows(BusinessException.class, () -> userService.getUserAddress(PUBLIC_USER_ID, PUBLIC_ADDRESS_ID));
    }

    @Test
    void test_request_password_reset_with_exist_user() {
        //given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateResetPasswordToken(anyString())).thenReturn(JWT_TOKEN);

        //act
        userService.resetPasswordRequest(EMAIL);

        //assert
        verify(resetPasswordTokenRepository, times(1)).save(any(ResetPasswordToken.class));
    }

    @Test
    void test_throw_exception_when_request_password_reset_with_non_exist_user() {
        //given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        //act & assert
        assertThrows(UserNotFoundException.class, () -> userService.resetPasswordRequest(EMAIL));
        verify(resetPasswordTokenRepository, never()).save(any(ResetPasswordToken.class));
    }

    @Test
    void test_update_password_reset_with_valid_token() {
        //given
        when(resetPasswordTokenRepository.findByToken(anyString())).thenReturn(Optional.of(resetPasswordToken));
        when(jwtService.isValidToken(anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn(ENCRYPTED_PASSWORD2);

        //act
        userService.resetPasswordUpdate(JWT_TOKEN, resetPasswordUpdateRequest);

        //assert
        verify(passwordEncoder, times(1)).encode(any());
        verify(resetPasswordTokenRepository, times(1)).save(any(ResetPasswordToken.class));
    }

    @Test
    void test_throw_exception_when_try_update_password_reset_with_non_existent_token() {
        //given
        when(resetPasswordTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

        //act & assert
        assertThrows(BusinessException.class, () -> userService.resetPasswordUpdate(JWT_TOKEN, resetPasswordUpdateRequest));
        verify(resetPasswordTokenRepository, never()).save(any(ResetPasswordToken.class));
    }

    @Test
    void test_throw_exception_when_try_update_password_reset_with_invalid_token() {
        //given
        when(resetPasswordTokenRepository.findByToken(anyString())).thenReturn(Optional.of(resetPasswordToken));
        when(jwtService.isValidToken(anyString())).thenReturn(false);

        //act & assert
        assertThrows(BusinessException.class, () -> userService.resetPasswordUpdate(JWT_TOKEN, resetPasswordUpdateRequest));
        verify(resetPasswordTokenRepository, never()).save(any(ResetPasswordToken.class));
    }

    @Test
    void test_throw_exception_when_try_update_password_reset_with_different_passwords() {
        //given
        when(resetPasswordTokenRepository.findByToken(anyString())).thenReturn(Optional.of(resetPasswordToken));
        when(jwtService.isValidToken(anyString())).thenReturn(true);
        resetPasswordUpdateRequest.setConfirmNewPassword("147");

        //act & assert
        assertThrows(BusinessException.class, () -> userService.resetPasswordUpdate(JWT_TOKEN, resetPasswordUpdateRequest));
        verify(resetPasswordTokenRepository, never()).save(any(ResetPasswordToken.class));
    }

    @Test
    void test_verify_email_token_with_valid_token() {
        //given
        when(userRepository.findUserByEmailVerificationToken(anyString())).thenReturn(Optional.of(user));
        when(jwtService.isValidToken(anyString())).thenReturn(true);
        user.setEmailVerificationStatus(false);

        //act
        userService.verifyEmailToken(JWT_TOKEN);

        //assert
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void test_verify_email_token_with_non_existent_token() {
        //given
        when(userRepository.findUserByEmailVerificationToken(anyString())).thenReturn(Optional.empty());

        //act & assert
        assertThrows(BusinessException.class, () -> userService.verifyEmailToken(JWT_TOKEN));
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void test_verify_email_token_with_email_already_validated() {
        //given
        when(userRepository.findUserByEmailVerificationToken(anyString())).thenReturn(Optional.of(user));
        user.setEmailVerificationStatus(true);

        //act & assert
        assertThrows(BusinessException.class, () -> userService.verifyEmailToken(JWT_TOKEN));
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void test_verify_email_token_with_invalid_token() {
        //given
        when(userRepository.findUserByEmailVerificationToken(anyString())).thenReturn(Optional.of(user));
        when(jwtService.isValidToken(anyString())).thenReturn(false);
        user.setEmailVerificationStatus(false);

        //act & assert
        assertThrows(BusinessException.class, () -> userService.verifyEmailToken(JWT_TOKEN));
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void test_get_all_user_addresses() {
        //given
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));

        List<AddressEntity> addresses = new ArrayList<>();
        addresses.add(address);
        user.setAddresses(addresses);

        List<AddressDto> addressesDto = new ArrayList<>();
        addressesDto.add(addressDto);
        userDto.setAddresses(addressesDto);

        Type listType = new TypeToken<List<AddressDto>>() {}.getType();
        when(modelMapper.map(user.getAddresses(), listType)).thenReturn(addressesDto);

        //act
        List<AddressDto> userAddresses = userService.getUserAddresses(PUBLIC_USER_ID);

        //assert
        assertThat(userAddresses).isNotEmpty();
        assertThat(userAddresses.size()).isEqualTo(1);
    }

    @Test
    void test_return_empty_list_when_try_to_get_all_user_addresses() {
        //given
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));

        List<AddressEntity> addresses = new ArrayList<>();
        user.setAddresses(addresses);

        //act
        List<AddressDto> userAddresses = userService.getUserAddresses(PUBLIC_USER_ID);

        //assert
        assertThat(userAddresses).isEmpty();
    }

    @Test
    void test_throw_exception_when_try_to_get_all_user_addresses_with_invalid_user_id() {
        //given
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());

        //act && assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserAddresses(PUBLIC_USER_ID));
        verify(userRepository, times(1)).findByUserId(anyString());
    }

    @Test
    void test_get_all_users() {
        //given
        List<UserEntity> users = new ArrayList<>();
        users.add(user);

        List<UserDto> usersDto = new ArrayList<>();
        usersDto.add(userDto);

        Page<UserEntity> page = new PageImpl<>(users);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        Type listType = new TypeToken<List<UserDto>>() {}.getType();
        when(modelMapper.map(users, listType)).thenReturn(usersDto);

        //act
        List<UserDto> allUsers = userService.getAllUsers(0, 1);

        //assert
        assertThat(allUsers).isNotEmpty();
        assertThat(allUsers.size()).isEqualTo(1);
    }
}
