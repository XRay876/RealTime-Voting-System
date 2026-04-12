package com.example.polls_service.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrentUser {
    private String userId;
    private String role;

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
}
