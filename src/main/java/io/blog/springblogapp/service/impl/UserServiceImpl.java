package io.blog.springblogapp.service.impl;

import io.blog.springblogapp.dto.AddressDto;
import io.blog.springblogapp.dto.UserDto;
import io.blog.springblogapp.exception.AddressNotFoundException;
import io.blog.springblogapp.exception.AuthException;
import io.blog.springblogapp.exception.BusinessException;
import io.blog.springblogapp.exception.UserNotFoundException;
import io.blog.springblogapp.model.entity.AddressEntity;
import io.blog.springblogapp.model.entity.ResetPasswordToken;
import io.blog.springblogapp.model.entity.RoleEntity;
import io.blog.springblogapp.model.entity.UserEntity;
import io.blog.springblogapp.model.enums.Roles;
import io.blog.springblogapp.model.request.ResetPasswordUpdateRequest;
import io.blog.springblogapp.repository.AddressRepository;
import io.blog.springblogapp.repository.ResetPasswordTokenRepository;
import io.blog.springblogapp.repository.RoleRepository;
import io.blog.springblogapp.repository.UserRepository;
import io.blog.springblogapp.service.JwtService;
import io.blog.springblogapp.service.UserService;
import io.blog.springblogapp.utils.ErrorMessages;
import io.blog.springblogapp.utils.Utils;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.*;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private AddressRepository addressRepository;
    private ResetPasswordTokenRepository resetPasswordTokenRepository;
    private Utils utils;
    private PasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;
    private JwtService jwtService;
    private RoleRepository roleRepository;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDTO) {
        verifyIfUserAlreadyExists(userDTO);

        for (AddressDto address : userDTO.getAddresses()) {
            address.setUser(userDTO);
            address.setAddressId(utils.generateAddressId());
        }

        UserEntity user = modelMapper.map(userDTO, UserEntity.class);

        String publicUserId = utils.generateUserId();
        user.setUserId(publicUserId);
        user.setEncryptedPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmailVerificationToken(jwtService.generateEmailTokenValidation(userDTO));
        user.setEmailVerificationStatus(false);

        Collection<RoleEntity> roles = new HashSet<>();
        Optional<RoleEntity> role = roleRepository.findByName(Roles.ROLE_USER.name());
        role.ifPresent(roles::add);

        user.setRoles(roles);

        UserEntity savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserDto.class);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUser(String email) {
        UserEntity user = findUserByEmail(email);

        return modelMapper.map(user, UserDto.class);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers(int page, int limit) {
        Pageable pageableRequest = PageRequest.of(page, limit);

        Page<UserEntity> userEntities = userRepository.findAll(pageableRequest);
        List<UserEntity> users = userEntities.toList();

        Type listType = new TypeToken<List<UserDto>>() {}.getType();

        return modelMapper.map(users, listType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDto> getUserAddresses(String userId) {
        UserEntity foundUser = findUserByUserId(userId);

        if (!foundUser.getAddresses().isEmpty()) {
            Type listType = new TypeToken<List<AddressDto>>() {}.getType();
            return modelMapper.map(foundUser.getAddresses(), listType);
        }

        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDto getUserAddress(String userId, String addressId) {
        UserEntity foundUser = findUserByUserId(userId);
        AddressEntity foundAddress = findByAddressId(addressId);

        if (!foundUser.equals(foundAddress.getUser())) {
            throw new BusinessException(ErrorMessages.ADDRESS_NOT_BELONG_USER.getErrorMessage());
        }

        return modelMapper.map(foundAddress, AddressDto.class);
    }

    @Override
    @Transactional
    public void verifyEmailToken(String token) {
        UserEntity user = getUserByEmailVerificationToken(token);

        if (user.getEmailVerificationStatus().equals(true)) {
            throw new BusinessException(ErrorMessages.EMAIL_ALREADY_VALIDATED.getErrorMessage());
        }

        if (!jwtService.isValidToken(token)) {
            throw new BusinessException(ErrorMessages.INVALID_TOKEN_VALIDATION.getErrorMessage());
        }

        user.setEmailVerificationToken(null);
        user.setEmailVerificationStatus(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPasswordRequest(String email) {
        UserEntity foundUser = findUserByEmail(email);

        String token = jwtService.generateResetPasswordToken(foundUser.getUserId());

        ResetPasswordToken resetToken = ResetPasswordToken
                .builder()
                .token(token)
                .user(foundUser)
                .build();

        resetPasswordTokenRepository.save(resetToken);
    }

    @Override
    @Transactional
    public void resetPasswordUpdate(String token, ResetPasswordUpdateRequest request) {
        ResetPasswordToken resetToken = findByResetToken(token);

        if (!jwtService.isValidToken(resetToken.getToken())) {
            throw new BusinessException(ErrorMessages.INVALID_TOKEN_VALIDATION.getErrorMessage());
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new BusinessException(ErrorMessages.INVALID_PASSWORD_RESET.getErrorMessage());
        }

        String newPassword = passwordEncoder.encode(request.getNewPassword());
        resetToken.getUser().setEncryptedPassword(newPassword);

        resetPasswordTokenRepository.save(resetToken);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity foundUser = findUserByUserId(userId);

        return modelMapper.map(foundUser, UserDto.class);
    }

    @Transactional
    @Override
    public UserDto updateUser(String userId, UserDto user) {
        UserEntity foundUser = findUserByUserId(userId);

        foundUser.setFirstName(user.getFirstName());
        foundUser.setLastName(user.getLastName());

        UserEntity updatedUser = userRepository.save(foundUser);

        return modelMapper.map(updatedUser, UserDto.class);
    }

    @Transactional
    @Override
    public void deleteUser(String userId) {
        UserEntity foundUser = findUserByUserId(userId);

        userRepository.delete(foundUser);
    }

    @Transactional(readOnly = true)
    public ResetPasswordToken findByResetToken(String resetToken) {
        Optional<ResetPasswordToken> foundToken = resetPasswordTokenRepository.findByToken(resetToken);
        if (foundToken.isEmpty()) {
            throw new BusinessException(ErrorMessages.NO_TOKEN_FOUND.getErrorMessage());
        }

        return foundToken.get();
    }

    @Transactional(readOnly = true)
    public void verifyIfUserAlreadyExists(UserDto userDTO) {
        Optional<UserEntity> result = userRepository.findByEmail(userDTO.getEmail());
        if (result.isPresent()) {
            throw new AuthException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());
        }
    }

    @Transactional(readOnly = true)
    public UserEntity findUserByEmail(String email) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UserNotFoundException(ErrorMessages.NO_RECORD_FOUND_USERNAME.getErrorMessage());
        }

        return user.get();
    }

    @Transactional(readOnly = true)
    public UserEntity findUserByUserId(String userId) {
        Optional<UserEntity> foundUser = userRepository.findByUserId(userId);
        if (foundUser.isEmpty()) {
            throw new UserNotFoundException(ErrorMessages.NO_RECORD_FOUND_ID.getErrorMessage());
        }

        return foundUser.get();
    }

    @Transactional(readOnly = true)
    public AddressEntity findByAddressId(String addressId) {
        Optional<AddressEntity> foundAddress = addressRepository.findByAddressId(addressId);
        if (foundAddress.isEmpty()) {
            throw new AddressNotFoundException(ErrorMessages.NO_RECORD_FOUND_ID.getErrorMessage());
        }

        return foundAddress.get();
    }

    @Transactional(readOnly = true)
    public UserEntity getUserByEmailVerificationToken(String token) {
        Optional<UserEntity> foundUser = userRepository.findUserByEmailVerificationToken(token);
        if (foundUser.isEmpty()) {
            throw new BusinessException(ErrorMessages.EMAIL_ADDRESS_NOT_VERIFIED.getErrorMessage());
        }

        return foundUser.get();
    }
}