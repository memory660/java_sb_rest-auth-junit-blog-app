package io.blog.springblogapp.model.response;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse extends RepresentationModel<AddressResponse> {
        
    private String addressId;

    private String city;

    private String country;

    private String streetName;

    private String postalCode;

    private String type;
}
