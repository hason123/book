package com.example.book.service;

import com.example.book.dto.RequestDTO.UserRoleUpdateDTO;
import com.example.book.dto.ResponseDTO.User.UserInfoDTO;
import com.example.book.entity.User;
import com.example.book.exception.NullValueException;

import java.util.List;

public interface UserService {


    UserInfoDTO createUser(User user);
    Object getUserById(Long id) throws NullValueException;
    void deleteUserById(Long id) throws NullValueException;
    UserInfoDTO updateUser(Long id, User user) throws NullValueException;

    List<User> getAllUsers();

    void updateRole(UserRoleUpdateDTO userRole);
}
