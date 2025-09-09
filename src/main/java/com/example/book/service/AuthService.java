package com.example.book.service;

import com.example.book.dto.RequestDTO.LoginRequestDTO;
import com.example.book.dto.RequestDTO.UserRequestDTO;
import com.example.book.dto.ResponseDTO.LoginResponseDTO;
import com.example.book.dto.ResponseDTO.User.UserInfoResponseDTO;
import com.example.book.exception.UnauthorizedException;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO request);

    LoginResponseDTO refreshToken(String refreshToken) throws UnauthorizedException;

    void logout();

    UserInfoResponseDTO register(UserRequestDTO request);
}
