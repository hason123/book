package com.example.book.controller;

import com.example.book.config.MessageConfig;
import com.example.book.dto.RequestDTO.LoginRequestDTO;
import com.example.book.dto.ResponseDTO.LoginResponseDTO;
import com.example.book.dto.RequestDTO.UserRequestDTO;
import com.example.book.entity.User;
import com.example.book.exception.UnauthorizedException;
import com.example.book.service.impl.UserServiceImpl;
import com.example.book.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
@Slf4j
@RestController
@RequestMapping("/api/v1/library")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserServiceImpl userServiceImpl;
    private final MessageConfig messageConfig;

    @Value("${hayson.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;
    @Value("${hayson.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;
    private final String TOKEN_NOT_EXISTED = "error.auth.token.refresh";
    private final String LOGOUT_SUCCESS = "success.user.logout";

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, UserServiceImpl userServiceImpl, MessageConfig messageConfig) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userServiceImpl = userServiceImpl;
        this.messageConfig = messageConfig;
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        log.info("User is attempting to login");
        //Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        //xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("Authentication successful for user");
        LoginResponseDTO requestDTO = new LoginResponseDTO();
        User currentUserDB = userServiceImpl.handleGetUserByUserName(loginRequest.getUsername());
        LoginResponseDTO.UserLogin UserLogin = new LoginResponseDTO.UserLogin();
        UserLogin.setId(currentUserDB.getUserId());
        UserLogin.setUsername(currentUserDB.getUserName());
        UserLogin.setRole(String.valueOf(currentUserDB.getRole().getRoleName()));
        requestDTO.setUser(UserLogin);
        // Tạo access token và refresh token
        String accesss_Token = this.securityUtil.createAccessToken(authentication.getName(), requestDTO);
        // Set access token vào đối tượng ResLoginDTO để trả về cho người dùng
        requestDTO.setAccessToken(accesss_Token);
        //create refresh token
        String refresh_token = securityUtil.createRefreshToken(loginRequest.getUsername(), requestDTO);
        //update user
        userServiceImpl.updateUserToken(refresh_token, loginRequest.getUsername());
        log.info("Successfully access & refresh tokens for user");
        //create cookie
        ResponseCookie springCookie = ResponseCookie.from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                .body(requestDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/auth/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "none") String refreshToken) throws UnauthorizedException
             {
        Jwt decodeToken = securityUtil.checkValidRefreshToken(refreshToken);
        String userName = decodeToken.getSubject();
        // SecurityUtil.getCurrentUserLogin().get()
                 //Co the can nhac check xem user voi token co hop le ko
        if(userName.equals("none")) {
            throw new UnauthorizedException(messageConfig.getMessage(TOKEN_NOT_EXISTED));
        }
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        User currentUserDB = this.userServiceImpl.handleGetUserByUserNameAndRefreshToken(userName, refreshToken);

        if(currentUserDB == null) {
            throw new UnauthorizedException(messageConfig.getMessage(TOKEN_NOT_EXISTED));
        }
        LoginResponseDTO.UserLogin userLogin = new LoginResponseDTO.UserLogin(
                currentUserDB.getUserId(),
                currentUserDB.getUserName(),
                currentUserDB.getRole().getRoleName().name()
        );
        loginResponseDTO.setUser(userLogin);
                 // Tạo access token và refresh token
        String accesss_Token = this.securityUtil.createAccessToken(userName, loginResponseDTO);
        // Set access token vào đối tượng ReqLoginDTO để trả về cho người dùng
        loginResponseDTO.setAccessToken(accesss_Token);
        String new_refresh_token = this.securityUtil.createRefreshToken(userName, loginResponseDTO);
        // Cập nhật refresh token vào database
        this.userServiceImpl.updateUserToken(new_refresh_token, refreshToken);
        // Tạo cookie
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .maxAge(refreshTokenExpiration)
                .path("/")
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(loginResponseDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(){
        Optional<String> currentUser = SecurityUtil.getCurrentUserLogin();
        String userName = currentUser.get();
        userServiceImpl.updateUserToken("", userName);
        log.info("User {} logged out successfully", userName);
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0) // Expire immediately
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(LOGOUT_SUCCESS);
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/auth/register")
    public ResponseEntity<UserRequestDTO> register(@Valid @RequestBody UserRequestDTO user) {
        log.info("User registration attempt!");
        UserRequestDTO userCreated = userServiceImpl.createUser(user);
        log.info("User registered successfully!");
        return ResponseEntity.ok(userCreated);
    }




}
