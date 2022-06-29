package io.blog.springblogapp.repository;

import io.blog.springblogapp.model.entity.AddressEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AddressRepositoryTest {

    private static final String PUBLIC_ADDRESS_ID = "W6FgnuAqy2EhptMv10aZBE6h8cnnILZLJM0rRDWy";
    private static final String ADDRESS_ID = "1c7864ba-f205-4c37-9bb6-5a99fc39f658";
    private static final String CITY = "Vancouver";
    private static final String COUNTRY = "Canada";
    private static final String POSTAL_CODE = "A85RP3W8";
    private static final String STREET_NAME = "138 Avenue Lost";
    private static final String TYPE = "BILLING";

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    TestEntityManager entityManager;

    AddressEntity address;

    @BeforeEach
    void setUp() {
        address = AddressEntity.builder().addressId(PUBLIC_ADDRESS_ID).city(CITY).country(COUNTRY).postalCode(POSTAL_CODE)
                .streetName(STREET_NAME).type(TYPE).build();
    }

    @Test
    void test_find_by_address_id() {
        //given
        entityManager.persist(address);

        //act
        Optional<AddressEntity> result = addressRepository.findByAddressId(PUBLIC_ADDRESS_ID);

        //assert
        assertThat(result).isNotNull();
        assertThat(result.isPresent()).isTrue();
    }
}
