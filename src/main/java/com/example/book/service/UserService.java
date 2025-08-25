package com.example.book.service;

import com.example.book.dto.RequestDTO.Search.SearchUserRequest;
import com.example.book.dto.RequestDTO.UserRequestDTO;
import com.example.book.dto.RequestDTO.UserRoleRequestDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.User.UserViewDTO;
import com.example.book.entity.User;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.exception.UnauthorizedException;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserRequestDTO createUser(UserRequestDTO userRequest);

    Object getUserById(Long id) throws UnauthorizedException;

    PageResponseDTO<UserViewDTO> getAllUsers(Pageable pageable);

    void deleteUserById(Long id) throws UnauthorizedException;

    UserRequestDTO updateUser(Long id, UserRequestDTO userRequest)  throws UnauthorizedException;

    void updateRole(UserRoleRequestDTO userRole) throws ResourceNotFoundException, UnauthorizedException;

    PageResponseDTO<UserViewDTO> searchUser(SearchUserRequest request, Pageable pageable);

    User getCurrentUser();

    User handleGetUserByUserName(String userName);
}
