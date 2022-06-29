package io.blog.springblogapp.model.request;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequest {

    private String city;

    private String country;

    private String streetName;

    private String postalCode;

    private String type;

}
