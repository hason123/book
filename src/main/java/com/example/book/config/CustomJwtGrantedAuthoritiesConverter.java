/*
package com.example.book.config;

import com.example.book.constant.RoleType;
import com.example.book.entity.Permission;
import com.example.book.entity.Role;
import com.example.book.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CustomJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final RoleRepository roleRepository;

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");

        Set<GrantedAuthority> authorities = new HashSet<>();

        if (roles != null) {
            for (String role : roles) {
                // Nếu role trong JWT là "ROLE_USER", thì ánh xạ sang RoleType.USER
                RoleType roleType = RoleType.valueOf(role.replace("ROLE_", ""));
                Optional<Role> roleEntity = roleRepository.findByRoleName(roleType);
                if (roleEntity.isPresent()) {
                    List<Permission> permissions = roleEntity.get().getPermissions();
                    for (Permission permission : permissions) {
                        authorities.add(new SimpleGrantedAuthority(permission.getName()));
                    }
                }
                // Thêm chính vai trò cũng vào luôn, nếu bạn muốn dùng hasRole()
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }

        return authorities;
    }
}

 */

