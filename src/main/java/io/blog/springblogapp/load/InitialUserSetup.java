package io.blog.springblogapp.load;

import io.blog.springblogapp.model.entity.AuthorityEntity;
import io.blog.springblogapp.model.entity.RoleEntity;
import io.blog.springblogapp.model.entity.UserEntity;
import io.blog.springblogapp.model.enums.Roles;
import io.blog.springblogapp.repository.AuthorityRepository;
import io.blog.springblogapp.repository.RoleRepository;
import io.blog.springblogapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@AllArgsConstructor
@Component
public class InitialUserSetup {

    private AuthorityRepository authorityRepository;
    private RoleRepository roleRepository;
    private UserRepository userRepository;

    @EventListener
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //authority
        AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
        AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
        AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");

        RoleEntity roleUser = createRole(Roles.ROLE_USER.name(), Arrays.asList(readAuthority, writeAuthority));
        RoleEntity roleAdmin = createRole(Roles.ROLE_ADMIN.name(), Arrays.asList(readAuthority, writeAuthority, deleteAuthority));

        Optional<UserEntity> user = userRepository.findByEmail("admin@admin.com");
        user.ifPresent(userEntity -> userEntity.setRoles(Collections.singletonList(roleAdmin)));

        Optional<UserEntity> user2 = userRepository.findByEmail("user@user.com");
        user2.ifPresent(userEntity -> userEntity.setRoles(Collections.singletonList(roleUser)));
    }

    private RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {
        Optional<RoleEntity> result = roleRepository.findByName(name);
        if (result.isEmpty()) {
            RoleEntity role = RoleEntity.builder().name(name).authorities(authorities).build();
            return roleRepository.save(role);
        }

        return result.get();
    }

    private AuthorityEntity createAuthority(String name) {
        Optional<AuthorityEntity> result = authorityRepository.findByName(name);
        if (result.isEmpty()) {
            AuthorityEntity authority = AuthorityEntity.builder().name(name).build();
            return authorityRepository.save(authority);
        }

        return result.get();
    }

}
