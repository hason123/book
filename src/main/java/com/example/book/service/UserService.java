package com.example.book.service;

import com.example.book.dto.RequestDTO.Search.SearchUserRequest;
import com.example.book.dto.RequestDTO.UserRequestDTO;
import com.example.book.dto.RequestDTO.UserRoleUpdateDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.User.UserViewDTO;
import com.example.book.exception.NullValueException;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.exception.UnauthorizedException;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserRequestDTO createUser(UserRequestDTO userRequest);

    Object getUserById(Long id) throws NullValueException, UnauthorizedException;

    PageResponseDTO<UserViewDTO> getAllUsers(Pageable pageable);

    void deleteUserById(Long id) throws NullValueException;

    UserRequestDTO updateUser(Long id, UserRequestDTO userRequest)  throws NullValueException, UnauthorizedException;

    void updateRole(UserRoleUpdateDTO userRole) throws ResourceNotFoundException, UnauthorizedException;

    PageResponseDTO<UserViewDTO> searchUser(SearchUserRequest request, Pageable pageable);
}
