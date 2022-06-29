package io.blog.springblogapp.controller;

import io.blog.springblogapp.model.request.UserLoginRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class AuthController {

    @ApiOperation("User Login")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Response Headers", responseHeaders = {
                    @ResponseHeader(name = "Authorization", description = "Bearer <JWT Token>", response = String.class),
                    @ResponseHeader(name = "UserId", description = "<Public User ID>", response = String.class)
            })
    })
    @PostMapping(value = "/api/v1/auth/login")
    public void fakeLoginMethod(@RequestBody UserLoginRequest request) {
        throw new IllegalStateException("This method should not be called. This method is implemented by Spring Security.");
    }

}
