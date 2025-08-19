package com.example.book.config;

import com.example.book.dto.ResponseDTO.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final MessageConfig messageConfig;
    private final static String TOKEN_INVALID_CODE = "error.auth.token.invalid";

    public RestAuthenticationEntryPoint(MessageConfig messageConfig) {
        this.messageConfig = messageConfig;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException{

        ApiResponse<?> errorResponse = new ApiResponse<>(
                401, messageConfig.getMessage(TOKEN_INVALID_CODE), null
        );
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}
