package io.blog.springblogapp.service;

import io.blog.springblogapp.dto.AddressDto;
import io.blog.springblogapp.dto.UserDto;
import io.blog.springblogapp.model.request.ResetPasswordUpdateRequest;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDTO);
    UserDto getUser(String email);
    UserDto getUserByUserId(String userId);
    UserDto updateUser(String userId, UserDto userDTO);
    void deleteUser(String userId);
    List<UserDto> getAllUsers(int page, int limit);
    List<AddressDto> getUserAddresses(String userid);
    AddressDto getUserAddress(String userId, String addressId);
    void verifyEmailToken(String token);
    void resetPasswordRequest(String email);
    void resetPasswordUpdate(String token, ResetPasswordUpdateRequest request);
}
