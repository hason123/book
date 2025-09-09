package com.example.book.service.impl;

import com.example.book.config.MessageConfig;
import com.example.book.constant.MessageError;
import com.example.book.dto.RequestDTO.LoginRequestDTO;
import com.example.book.dto.RequestDTO.UserRequestDTO;
import com.example.book.dto.ResponseDTO.LoginResponseDTO;
import com.example.book.dto.ResponseDTO.User.UserInfoResponseDTO;
import com.example.book.entity.User;
import com.example.book.exception.UnauthorizedException;
import com.example.book.service.AuthService;
import com.example.book.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserServiceImpl userServiceImpl;
    private final MessageConfig messageConfig;

    public AuthServiceImpl(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, UserServiceImpl userServiceImpl, MessageConfig messageConfig) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userServiceImpl = userServiceImpl;
        this.messageConfig = messageConfig;
    }
    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        log.info("User is attempting to login");
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("Authentication successful for user");

        User currentUserDB = userServiceImpl.handleGetUserByUserName(request.getUsername());

        LoginResponseDTO.UserLogin userLogin = new LoginResponseDTO.UserLogin();
        userLogin.setId(currentUserDB.getUserId());
        userLogin.setUsername(currentUserDB.getUserName());
        userLogin.setRole(String.valueOf(currentUserDB.getRole().getRoleName()));

        LoginResponseDTO response = new LoginResponseDTO();
        response.setUser(userLogin);

        // Generate tokens
        String accessToken = securityUtil.createAccessToken(authentication.getName(), response);
        String refreshToken = securityUtil.createRefreshToken(request.getUsername(), response);

        // Update refresh token in DB
        userServiceImpl.updateUserToken(refreshToken, request.getUsername());

        // Set tokens in DTO
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);

        return response;
    }

    @Override
    public LoginResponseDTO refreshToken(String oldRefreshToken) throws UnauthorizedException {
        Jwt decodeToken = securityUtil.checkValidRefreshToken(oldRefreshToken);
        String userName = decodeToken.getSubject();

        if ("none".equals(userName)) {
            throw new UnauthorizedException(messageConfig.getMessage(MessageError.TOKEN_NOT_EXISTED));
        }

        User user = userServiceImpl.handleGetUserByUserNameAndRefreshToken(userName, oldRefreshToken);

        if (user == null) {
            throw new UnauthorizedException(messageConfig.getMessage(MessageError.TOKEN_NOT_EXISTED));
        }

        LoginResponseDTO.UserLogin userLogin = new LoginResponseDTO.UserLogin(
                user.getUserId(),
                user.getUserName(),
                user.getRole().getRoleName().name()
        );
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setUser(userLogin);

        // Generate new access token
        String accessToken = securityUtil.createAccessToken(userName, loginResponseDTO);
        loginResponseDTO.setAccessToken(accessToken);

        // Generate new refresh token
        String newRefreshToken = securityUtil.createRefreshToken(userName, loginResponseDTO);
        loginResponseDTO.setRefreshToken(newRefreshToken); // This field is @JsonIgnore

        // Update refresh token in database
        userServiceImpl.updateUserToken(newRefreshToken, userName);

        return loginResponseDTO;
    }

    @Override
    public void logout() {
        String userName = userServiceImpl.getCurrentUser().getUserName();
        userServiceImpl.updateUserToken("", userName);
    }

    @Override
    public UserInfoResponseDTO register(UserRequestDTO request) {
        return userServiceImpl.createUser(request);
    }
}
