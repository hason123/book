package com.example.book.config;

import com.example.book.dto.ResponseDTO.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;


@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    private final MessageConfig messageConfig;
    private final static String ACCESS_DENIED = "error.auth.access.denied";

    public RestAccessDeniedHandler(MessageConfig messageConfig) {
        this.messageConfig = messageConfig;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ApiResponse<?> errorResponse = new ApiResponse<>(
                403, messageConfig.getMessage(ACCESS_DENIED), null
        );
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));

    }
}
