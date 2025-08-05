package com.example.book.config;


import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import com.example.book.entity.User;
import com.example.book.service.impl.UserServiceImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;



@Component("userDetailCustom")
public class UserDetailCustom  implements UserDetailsService{

    private final UserServiceImpl userService;

    public UserDetailCustom(UserServiceImpl userService) {
        this.userService = userService;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.handleGetUserByUserName(username);

        String roleName = user.getRole().getRoleName().toString();

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleName);

        return new CustomUser(
                user.getUserId(), // assuming it's UUID
                user.getUserName(),
                user.getPassword(),
                List.of(authority)
        );
    }
}
