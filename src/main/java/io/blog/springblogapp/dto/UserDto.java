package io.blog.springblogapp.dto;

import lombok.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable {


    private static final long serialVersionUID = 1L;

    private UUID id;

    private String userId;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String encryptedPassword;

    private String emailVerificationToken;

    private Boolean emailVerificationStatus = false;

    private List<AddressDto> addresses;

    private Collection<String> roles;

}
