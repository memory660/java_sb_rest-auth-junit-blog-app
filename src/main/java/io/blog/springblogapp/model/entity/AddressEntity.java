package io.blog.springblogapp.model.entity;

import io.blog.springblogapp.dto.UserDto;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "tb_addresses")
public class AddressEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    private UUID id;

    @Column(nullable = false, length = 40)
    private String addressId;

    @Column(nullable = false, length = 40)
    private String city;

    @Column(nullable = false, length = 40)
    private String country;

    @Column(nullable = false, length = 150)
    private String streetName;

    @Column(nullable = false, length = 15)
    private String postalCode;

    @Column(nullable = false, length = 20)
    private String type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
