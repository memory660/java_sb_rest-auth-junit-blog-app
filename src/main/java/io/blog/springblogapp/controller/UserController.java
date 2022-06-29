package io.blog.springblogapp.controller;

import io.blog.springblogapp.dto.AddressDto;
import io.blog.springblogapp.dto.UserDto;
import io.blog.springblogapp.model.request.ResetPasswordRequest;
import io.blog.springblogapp.model.request.ResetPasswordUpdateRequest;
import io.blog.springblogapp.model.request.UserDetailsRequest;
import io.blog.springblogapp.model.request.UserUpdateRequest;
import io.blog.springblogapp.model.response.AddressResponse;
import io.blog.springblogapp.model.response.UserResponse;
import io.blog.springblogapp.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping(value = "/email-verification", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<Void> verifyEmailToken(@RequestParam(value = "token") String token) {
        userService.verifyEmailToken(token);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(value = "/reset-password-request",
                 produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
                 consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<Void> resetPasswordRequest(@RequestBody ResetPasswordRequest request) {
        userService.resetPasswordRequest(request.getEmail());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(value = "/reset-password",
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<Void> resetPassword(@RequestParam(value = "token") String token,
                                              @RequestBody ResetPasswordUpdateRequest request) {

        userService.resetPasswordUpdate(token, request);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(value = "/{id}/addresses", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<CollectionModel<AddressResponse>> getUserAddresses(@PathVariable("id") String userId) {
        List<AddressDto> foundAddresses = userService.getUserAddresses(userId);

        Type listType = new TypeToken<List<AddressResponse>>() {}.getType();
        List<AddressResponse> addresses = modelMapper.map(foundAddresses, listType);

        for (AddressResponse address : addresses) {
            Link selfLink = linkTo(methodOn(UserController.class)
                    .getUserAddress(userId, address.getAddressId())).withSelfRel();

            address.add(selfLink);
        }

        Link userLink = linkTo(methodOn(UserController.class).getUser(userId)).withRel("user");
        Link selfLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withSelfRel();

        CollectionModel<AddressResponse> response = CollectionModel.of(addresses, userLink, selfLink);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(value = "/{id}/addresses/{addressId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<AddressResponse> getUserAddress(@PathVariable("id") String userId,
                                                          @PathVariable("addressId") String addressId) {

        AddressDto foundAddresses = userService.getUserAddress(userId, addressId);

        AddressResponse response = modelMapper.map(foundAddresses, AddressResponse.class);

        Link userLink = linkTo(methodOn(UserController.class).getUser(userId)).withRel("user");
        Link userAddressLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
        Link selfLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();

        response.add(userLink, userAddressLink, selfLink);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(value = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") String userId) {
        UserDto foundUser = userService.getUserByUserId(userId);

        UserResponse response = modelMapper.map(foundUser, UserResponse.class);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<List<UserResponse>> getAllUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                          @RequestParam(value = "limit", defaultValue = "15") int limit) {

        List<UserDto> allUsers = userService.getAllUsers(page, limit);

        Type listType = new TypeToken<List<UserResponse>>() {}.getType();
        List<UserResponse> response = modelMapper.map(allUsers, listType);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
                 consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<UserResponse> createUser(@RequestBody UserDetailsRequest request) {
        UserDto userDto = modelMapper.map(request, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);

        UserResponse response = modelMapper.map(createdUser, UserResponse.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @PutMapping(value = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
                                 consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<UserResponse> updateUser(@PathVariable("id") String userId,
                                                   @RequestBody UserUpdateRequest request) {

        UserDto userDTO = modelMapper.map(request, UserDto.class);

        UserDto updatedUser = userService.updateUser(userId, userDTO);

        UserResponse response = modelMapper.map(updatedUser, UserResponse.class);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @DeleteMapping(value = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String userId) {
        userService.deleteUser(userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
