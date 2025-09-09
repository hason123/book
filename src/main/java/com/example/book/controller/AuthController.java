package com.example.book.controller;

import com.example.book.constant.MessageError;
import com.example.book.dto.RequestDTO.LoginRequestDTO;
import com.example.book.dto.ResponseDTO.LoginResponseDTO;
import com.example.book.dto.RequestDTO.UserRequestDTO;
import com.example.book.dto.ResponseDTO.User.UserInfoResponseDTO;
import com.example.book.exception.UnauthorizedException;
import com.example.book.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/api/v1/library")
public class AuthController {

    private final AuthService authService;

    @Value("${hayson.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Nguời dùng đăng nhập")
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = authService.login(loginRequest);
        // Create cookie using refreshToken from response
        ResponseCookie springCookie = ResponseCookie.from("refresh_token", response.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        // Remove refreshToken from response body if you don't want to return it as JSON
        response.setRefreshToken(null);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                .body(response);
    }

    @Operation(summary = "Refresh Token")
    @PutMapping("/auth/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "none") String refreshToken) throws UnauthorizedException {
        LoginResponseDTO response = authService.refreshToken(refreshToken);
        ResponseCookie cookie = ResponseCookie
                .from("refresh_token", response.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .maxAge(refreshTokenExpiration)
                .path("/")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @Operation(summary = "Nguời dùng đăng xuất")
    @PutMapping("/auth/logout")
    public ResponseEntity<?> logout(){
        authService.logout();
        return ResponseEntity.ok().body(MessageError.LOGOUT_SUCCESS);
    }

    @Operation(summary = "Người dùng đăng ký")
    @PostMapping("/auth/register")
    public ResponseEntity<UserInfoResponseDTO> register(@Valid @RequestBody UserRequestDTO user) {
        UserInfoResponseDTO response = authService.register(user);
        return ResponseEntity.ok(response);
    }




}
