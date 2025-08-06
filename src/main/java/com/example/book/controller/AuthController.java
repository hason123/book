package com.example.book.controller;


import com.example.book.dto.RequestDTO.LoginDTO;
import com.example.book.dto.RequestDTO.ReqLoginDTO;
import com.example.book.dto.ResponseDTO.User.UserInfoDTO;
import com.example.book.entity.User;
import com.example.book.exception.UnauthorizedException;
import com.example.book.service.impl.UserServiceImpl;
import com.example.book.utils.SecurityUtil;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final SecurityUtil securityUtil;
    private final UserServiceImpl userServiceImpl;

    @Value("${hayson.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @Value("${hayson.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, UserServiceImpl userServiceImpl) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        //Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        //xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        ReqLoginDTO requestDTO = new ReqLoginDTO();

        User currentUserDB = userServiceImpl.handleGetUserByUserName(loginDTO.getUsername());
        ReqLoginDTO.UserLogin UserLogin = new ReqLoginDTO.UserLogin();
        UserLogin.setId(currentUserDB.getUserId());
        UserLogin.setUsername(currentUserDB.getUserName());
        UserLogin.setRole(String.valueOf(currentUserDB.getRole().getRoleName()));
        requestDTO.setUser(UserLogin);

        // Tạo access token và refresh token
        String accesss_Token = this.securityUtil.createAccessToken(authentication.getName(), requestDTO);
        // Set access token vào đối tượng ResLoginDTO để trả về cho người dùng
        requestDTO.setAccessToken(accesss_Token);
        //create refresh token
        String refresh_token = securityUtil.createRefreshToken(loginDTO.getUsername(), requestDTO);
        //update user
        userServiceImpl.updateUserToken(refresh_token, loginDTO.getUsername());
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

   
    @GetMapping("/refresh")
    public ResponseEntity<ReqLoginDTO> refreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "none") String refreshToken) throws UnauthorizedException
             {
        Jwt decodeToken = securityUtil.checkValidRefreshToken(refreshToken);
        String userName = decodeToken.getSubject();
        // SecurityUtil.getCurrentUserLogin().get()
                 //Co the can nhac check xem user voi token co hop le ko
        if(userName.equals("none")) {
            throw new UnauthorizedException("No refresh token in cookie");
        }
        ReqLoginDTO reqLoginDTO = new ReqLoginDTO();
        User currentUserDB = this.userServiceImpl.handleGetUserByUserNameAndRefreshToken(userName, refreshToken);

        if(currentUserDB == null) {
            throw new UnauthorizedException("No refresh token in cookie");
        }
        ReqLoginDTO.UserLogin userLogin = new ReqLoginDTO.UserLogin(
                currentUserDB.getUserId(),
                currentUserDB.getUserName(),
                currentUserDB.getRole().getRoleName().name()
        );

        reqLoginDTO.setUser(userLogin);
                 // Tạo access token và refresh token
        String accesss_Token = this.securityUtil.createAccessToken(userName, reqLoginDTO);
        // Set access token vào đối tượng ReqLoginDTO để trả về cho người dùng
        reqLoginDTO.setAccessToken(accesss_Token);
        String new_refresh_token = this.securityUtil.createRefreshToken(userName, reqLoginDTO);
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

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(reqLoginDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() throws UnauthorizedException {
        Optional<String> currentUser = SecurityUtil.getCurrentUserLogin();

        String userName = currentUser.get();
        if(userName.equals("anonymousUser")) {
            throw new UnauthorizedException("User is not logged in. Please send your access token");
        }
        userServiceImpl.updateUserToken("", userName);  // "" means delete token

        System.out.println(userName);
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0) // Expire immediately
                .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body("User" + userName + "logged out");
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/register")
    public ResponseEntity<UserInfoDTO> register(@RequestBody User user) {
        UserInfoDTO userCreated = userServiceImpl.createUser(user);
        return ResponseEntity.ok(userCreated);
    }




}
