package org.humber.authuserservice.dto.response;
import org.humber.authuserservice.entity.User;

import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        UUID id,
        String email,
        String firstName,
        String lastName,
        String role
) {
    public AuthResponse(String accessToken, String refreshToken, User user) {
        this(
                accessToken,
                refreshToken,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name()
        );
    }
}