package io.blog.springblogapp.model.response;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
        
    private String userId;

    private String firstName;

    private String lastName;

    private String email;

    private List<AddressResponse> addresses;

}
