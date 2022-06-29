package io.blog.springblogapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.blog.springblogapp.dto.AddressDto;
import io.blog.springblogapp.dto.UserDto;
import io.blog.springblogapp.model.entity.*;
import io.blog.springblogapp.model.enums.Roles;
import io.blog.springblogapp.model.request.*;
import io.blog.springblogapp.model.response.AddressResponse;
import io.blog.springblogapp.model.response.UserResponse;
import io.blog.springblogapp.repository.UserRepository;
import io.blog.springblogapp.service.impl.JwtServiceImpl;
import io.blog.springblogapp.service.impl.UserDetailsServiceImpl;
import io.blog.springblogapp.service.impl.UserServiceImpl;
import io.blog.springblogapp.utils.Utils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.lang.reflect.Type;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String ID = "0d7403bc-0ba6-47c9-9499-11aebfff6969";
    private static final String PUBLIC_USER_ID = "AilzjCjfKFXq9PQSHMFxa6EXG6cC6cdKLfC6d7Cb";
    private static final String FIRST_NAME = "Éric";
    private static final String LAST_NAME = "Magalhães";
    private static final String EMAIL = "test@test.com";
    private static final String PASSWORD = "123";
    private static final String ENCRYPTED_PASSWORD = "$2a$10$RToYv0ZYYBVVjdL6oftLheXszKlAef/WzhsD2It3eWY44XkASpXVu"; //123
    private static final String JWT_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0ZUBlbWFpbC5jb20iLCJleHAiOjE2NTMwMDY3NTh9.JBPbOXQpNHRDqcax2ItLeBuxK8akpzElwdpu62UZfPNEVighXOLamHuZeGpPlLv3rtrCgoieN9tsVSDfWOGC6g";
    private static final String PUBLIC_ADDRESS_ID = "W6FgnuAqy2EhptMv10aZBE6h8cnnILZLJM0rRDWy";
    private static final String ADDRESS_ID = "1c7864ba-f205-4c37-9bb6-5a99fc39f658";
    private static final String CITY = "Vancouver";
    private static final String COUNTRY = "Canada";
    private static final String POSTAL_CODE = "A85RP3W8";
    private static final String STREET_NAME = "138 Avenue Lost";
    private static final String TYPE = "BILLING";
    private static final String API = "/api/v1/users";
    private static final String JWT_TOKEN_VALID = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGVtYWlsLmNvbSIsInVzZXJJZCI6IjJEY0o4YUdDTTVkRVJzMU1oYlFjc3Z5MEtoTHFhMTk5RDE1b0habEsiLCJleHAiOjMyMzE4MzAzNDF9.phSTqexKpKdqiHBLqfnDd2gPg5kWWH4VmLxkykm73dCsk4cMcmk80mePlpsRyYnX8XyXazpIH0hBFpEl67r-Iw";

    @Autowired
    MockMvc mvc;
    @MockBean
    UserServiceImpl userService;
    @MockBean
    UserRepository userRepository;
    @MockBean
    UserDetailsServiceImpl userDetailsService;
    @MockBean
    ModelMapper modelMapper;
    @MockBean
    PasswordEncoder passwordEncoder;
    @MockBean
    JwtServiceImpl jwtService;
    @MockBean
    Utils utils;
    @MockBean
    AuthenticationManager authenticationManager;

    UserEntity user;
    UserDto userDto;
    UserResponse userResponse;
    UserDetailsRequest userCreateRequest;
    UserUpdateRequest userUpdateRequest;
    UserLoginRequest userLoginRequest;
    AddressEntity address;
    AddressDto addressDto;
    AddressResponse addressResponse;
    AddressRequest addressRequest;
    ResetPasswordToken resetPasswordToken;
    ResetPasswordUpdateRequest resetPasswordUpdateRequest;
    ResetPasswordRequest resetPasswordRequest;
    AuthorityEntity readAuthority;
    AuthorityEntity writeAuthority;
    AuthorityEntity deleteAuthority;
    RoleEntity roleUser;
    RoleEntity roleAdmin;

    @BeforeEach
    void setUp() {
        readAuthority = AuthorityEntity.builder().name("READ_AUTHORITY").build();
        writeAuthority = AuthorityEntity.builder().name("WRITE_AUTHORITY").build();
        deleteAuthority = AuthorityEntity.builder().name("DELETE_AUTHORITY").build();

        roleUser = RoleEntity.builder().name(Roles.ROLE_USER.name()).authorities(Arrays.asList(readAuthority, writeAuthority)).build();
        roleAdmin = RoleEntity.builder().name(Roles.ROLE_ADMIN.name()).authorities(Arrays.asList(readAuthority, writeAuthority, deleteAuthority)).build();

        user = UserEntity.builder()
                .id(UUID.fromString(ID)).userId(PUBLIC_USER_ID).firstName(FIRST_NAME).lastName(LAST_NAME)
                .email(EMAIL).encryptedPassword(ENCRYPTED_PASSWORD).build();

        userDto = UserDto.builder()
                .id(UUID.fromString(ID)).userId(PUBLIC_USER_ID).firstName(FIRST_NAME).lastName(LAST_NAME)
                .email(EMAIL).encryptedPassword(ENCRYPTED_PASSWORD).build();

        userResponse = UserResponse.builder()
                .userId(PUBLIC_USER_ID).firstName(FIRST_NAME).lastName(LAST_NAME).email(EMAIL).build();

        userCreateRequest = UserDetailsRequest.builder().firstName(FIRST_NAME).lastName(LAST_NAME)
                .email(EMAIL).password(PASSWORD).build();

        userUpdateRequest = UserUpdateRequest.builder().firstName(FIRST_NAME).lastName(LAST_NAME).build();

        userLoginRequest = UserLoginRequest.builder().email(EMAIL).password(PASSWORD).build();

        addressResponse = AddressResponse.builder()
                .addressId(PUBLIC_ADDRESS_ID).city(CITY).country(COUNTRY).streetName(STREET_NAME)
                .postalCode(POSTAL_CODE).type(TYPE).build();

        address = AddressEntity.builder()
                .id(UUID.fromString(ADDRESS_ID)).addressId(PUBLIC_ADDRESS_ID).city(CITY).country(COUNTRY)
                .postalCode(POSTAL_CODE).streetName(STREET_NAME).type(TYPE).user(user).build();

        addressDto = AddressDto.builder()
                .id(UUID.fromString(ADDRESS_ID)).addressId(PUBLIC_ADDRESS_ID).city(CITY).country(COUNTRY)
                .postalCode(POSTAL_CODE).streetName(STREET_NAME).type(TYPE).user(userDto).build();

        addressRequest = AddressRequest.builder().city(CITY).country(COUNTRY).streetName(STREET_NAME)
                .postalCode(POSTAL_CODE).type(TYPE).build();

        resetPasswordToken = ResetPasswordToken.builder().id(UUID.fromString(ID)).token(JWT_TOKEN).user(user).build();

        resetPasswordUpdateRequest = ResetPasswordUpdateRequest.builder().newPassword("123").confirmNewPassword("123").build();

        resetPasswordRequest = ResetPasswordRequest.builder().email(EMAIL).build();
    }

    @Test
    void test_get_user_address() throws Exception {
        //given
        user.setRoles(Collections.singletonList(roleUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userService.getUserAddress(anyString(), anyString())).thenReturn(addressDto);
        when(modelMapper.map(addressDto, AddressResponse.class)).thenReturn(addressResponse);
        when(jwtService.isValidToken(anyString())).thenReturn(true);
        when(jwtService.getUsername(anyString())).thenReturn(EMAIL);

        //act
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(API.concat("/{id}/addresses/{addressId}"), PUBLIC_USER_ID, PUBLIC_ADDRESS_ID)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + JWT_TOKEN_VALID);

        //assert
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("addressId").value(PUBLIC_ADDRESS_ID))
                .andExpect(jsonPath("type").value(TYPE));
    }

    //throw

    @Test
    void test_get_user_addresses() throws Exception {
        //given
        List<AddressDto> addresses = List.of(addressDto);
        List<AddressResponse> addressesResponse = List.of(addressResponse);
        Type listType = new TypeToken<List<AddressResponse>>() {}.getType();

        when(userService.getUserAddresses(anyString())).thenReturn(addresses);
        when(modelMapper.map(addresses, listType)).thenReturn(addressesResponse);

        user.setRoles(Collections.singletonList(roleUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.isValidToken(anyString())).thenReturn(true);
        when(jwtService.getUsername(anyString())).thenReturn(EMAIL);

        //act
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(API.concat("/{id}/addresses"), PUBLIC_USER_ID)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + JWT_TOKEN_VALID);

        //assert
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.addressResponseList.[0].addressId").value(PUBLIC_ADDRESS_ID))
                .andExpect(jsonPath("_embedded.addressResponseList.[0].type").value(TYPE));
    }

    //throw

    @Test
    void test_get_user() throws Exception {
        //given
        List<AddressResponse> addresses = List.of(addressResponse);
        userResponse.setAddresses(addresses);

        user.setRoles(Collections.singletonList(roleUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userService.getUserByUserId(anyString())).thenReturn(userDto);
        when(modelMapper.map(userDto, UserResponse.class)).thenReturn(userResponse);
        when(jwtService.isValidToken(anyString())).thenReturn(true);
        when(jwtService.getUsername(anyString())).thenReturn(EMAIL);

        //act
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(API.concat("/{id}"), PUBLIC_USER_ID)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + JWT_TOKEN_VALID);

        //assert
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("firstName").value(FIRST_NAME));
    }

    //throw

    @Test
    void test_email_verification() throws Exception {
        //act
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(API.concat("/email-verification"))
                .param("token", JWT_TOKEN_VALID)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        //assert
        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    //throw

    @Test
    void test_reset_password_request() throws Exception {
        //given
        String content = new ObjectMapper().writeValueAsString(resetPasswordRequest);

        //act
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/reset-password-request"))
                .content(content)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        //assert
        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    //throw

    @Test
    void test_reset_password_update() throws Exception {
        //given
        String content = new ObjectMapper().writeValueAsString(resetPasswordUpdateRequest);

        //act
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/reset-password"))
                .param("token", JWT_TOKEN_VALID)
                .content(content)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        //assert
        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    //throw

    @Test
    void test_get_all_users() throws Exception {
        //given
        List<UserDto> users = List.of(userDto);
        List<UserResponse> usersResponse = List.of(userResponse);
        Type listType = new TypeToken<List<UserResponse>>() {}.getType();

        user.setRoles(Collections.singletonList(roleUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userService.getAllUsers(anyInt(), anyInt())).thenReturn(users);
        when(jwtService.isValidToken(anyString())).thenReturn(true);
        when(jwtService.getUsername(anyString())).thenReturn(EMAIL);
        when(modelMapper.map(users, listType)).thenReturn(usersResponse);

        //act
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(API)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + JWT_TOKEN_VALID);

        //assert
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.[0].userId").value(userResponse.getUserId()));
    }

    //throw

    @Test
    void test_create_new_user() throws Exception {
        //given
        String content = new ObjectMapper().writeValueAsString(userCreateRequest);

        when(modelMapper.map(userCreateRequest, UserDto.class)).thenReturn(userDto);
        when(userService.createUser(any())).thenReturn(userDto);
        when(modelMapper.map(userDto, UserResponse.class)).thenReturn(userResponse);

        //act
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content);

        //assert
        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("userId").value(PUBLIC_USER_ID))
                .andExpect(jsonPath("firstName").value(FIRST_NAME));
    }

    //throw

    @Test
    void test_update_user() throws Exception {
        //given
        String content = new ObjectMapper().writeValueAsString(userCreateRequest);

        when(modelMapper.map(userUpdateRequest, UserDto.class)).thenReturn(userDto);
        when(userService.updateUser(anyString(), any())).thenReturn(userDto);
        when(modelMapper.map(userDto, UserResponse.class)).thenReturn(userResponse);

        user.setRoles(Collections.singletonList(roleUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.isValidToken(anyString())).thenReturn(true);
        when(jwtService.getUsername(anyString())).thenReturn(EMAIL);

        //act
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(API.concat("/{id}"), PUBLIC_USER_ID)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + JWT_TOKEN_VALID);

        //assert
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("userId").value(PUBLIC_USER_ID))
                .andExpect(jsonPath("firstName").value(FIRST_NAME));
    }

    //throw

    @Test
    void test_delete_user() throws Exception {
        //given
        String content = new ObjectMapper().writeValueAsString(userCreateRequest);

        user.setRoles(Collections.singletonList(roleAdmin));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.isValidToken(anyString())).thenReturn(true);
        when(jwtService.getUsername(anyString())).thenReturn(EMAIL);

        //act
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(API.concat("/{id}"), PUBLIC_USER_ID)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + JWT_TOKEN_VALID);

        //assert
        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    //throw
}
