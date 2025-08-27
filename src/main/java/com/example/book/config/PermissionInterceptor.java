package com.example.book.config;

import com.example.book.entity.Permission;
import com.example.book.entity.Role;
import com.example.book.entity.User;
import com.example.book.exception.UnauthorizedException;
import com.example.book.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import java.util.List;
/*
@Transactional
public class PermissionInterceptor implements HandlerInterceptor {

    private final UserServiceImpl userService;
    private final MessageConfig messageConfig;
    private final static String ACCESS_DENIED = "error.auth.accessDenied";

    public PermissionInterceptor(UserServiceImpl userService, MessageConfig messageConfig) {
        this.userService = userService;
        this.messageConfig = messageConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String method = request.getMethod();

        User user = userService.getCurrentUser();
            if(user != null) {
                Role role = user.getRole();
                if(role != null) {
                    List<Permission> permissions = user.getRole().getPermissions();
                    boolean hasPermission = permissions.stream().anyMatch(
                            p -> p.getApiPath().equals(path) && p.getMethod().equals(method)
                    );
                    if(!hasPermission) {
                        throw new UnauthorizedException(messageConfig.getMessage(ACCESS_DENIED));
                    }
                } else {
                    throw new UnauthorizedException(messageConfig.getMessage(ACCESS_DENIED));
                }
            }


        return true;
    }
}

 */