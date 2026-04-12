package com.example.polls_service.security;

import com.example.polls_service.exception.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class RequestUserContext {

    public CurrentUser getCurrentUser(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");

        if (userId == null || userId.isBlank()) {
            throw new ForbiddenException("Missing X-User-Id header");
        }

        if (role == null || role.isBlank()) {
            role = "USER";
        }

        return new CurrentUser(userId, role);
    }
}
