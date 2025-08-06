package com.example.book.controller;

import com.example.book.dto.ResponseDTO.User.UserInfoDTO;

import com.example.book.entity.User;
import com.example.book.exception.NullValueException;
import com.example.book.service.impl.UserServiceImpl;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/library")
public class UserController {

    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserServiceImpl userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    //GetAll
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/user")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build(); // HTTP 204
        }
        return ResponseEntity.ok(users); // HTTP 200 + JSON list
    }
    //GetUserID

    //cach nay ko duoc do dung OAuth2
    //@PreAuthorize("hasAnyRole('ADMIN', 'USER') and #id == authentication.principal.id")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) throws NullValueException {
        User user = (User) userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    //DeleteUser
    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) throws NullValueException {
        userService.deleteUserById(id);
        return ResponseEntity.ok("Delete successful");
    }

    @PostMapping("/user/create")
    public ResponseEntity<UserInfoDTO> createUser(@RequestBody User user) {
        UserInfoDTO userAdded = userService.createUser(user);
        //return ResponseEntity.ok(userAdded);
        return ResponseEntity.ok().body(userAdded);
    }

    @PutMapping("/user/update/{id}")
    public ResponseEntity<UserInfoDTO> updateUser(@PathVariable Long id,
                                           @RequestBody User user) throws NullValueException {
        UserInfoDTO updatedUser = userService.updateUser(id, user);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }


}
