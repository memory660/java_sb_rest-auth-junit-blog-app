package io.blog.springblogapp.model.request;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsRequest {

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private List<AddressRequest> addresses;

}
