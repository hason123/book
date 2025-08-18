package com.example.book.config;

import com.example.book.dto.ResponseDTO.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final MessageSource messageSource;
    private final static String ACCESS_DENIED = "error.auth.accessdenied";

    public RestAccessDeniedHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ApiResponse<?> errorResponse = new ApiResponse<>(
                403, this.getMessage(ACCESS_DENIED), null
        );
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));

    }
}
