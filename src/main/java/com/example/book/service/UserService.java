package com.example.book.service;

import com.example.book.dto.ResponseDTO.User.UserDTO;
import com.example.book.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {


    UserDTO createUser(User user);
    User getUserById(Long id);
    void deleteUserById(Long id);
    User updateUser(Long id, User user);

    List<User> getAllUsers();

}
