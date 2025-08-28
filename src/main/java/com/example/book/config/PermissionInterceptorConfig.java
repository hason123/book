package com.example.book.config;

import com.example.book.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfig implements WebMvcConfigurer {

    private final UserServiceImpl userService;
    private final MessageConfig messageConfig;

    public PermissionInterceptorConfig(UserServiceImpl userService, MessageConfig messageConfig) {
        this.userService = userService;
        this.messageConfig = messageConfig;
    }

    @Bean
    public PermissionInterceptor permissionInterceptor() {
        return new PermissionInterceptor(userService, messageConfig);
    }

    private static final String[] whiteList = {
            "/", "/api/v1/library/auth/login", "/api/v1/library/auth/register",
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor())
                .excludePathPatterns(whiteList);
    }
}



