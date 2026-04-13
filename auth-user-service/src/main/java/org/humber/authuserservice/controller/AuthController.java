package org.humber.authuserservice.controller;

import org.humber.authuserservice.dto.request.LoginRequest;
import org.humber.authuserservice.dto.request.RegisterRequest;
import org.humber.authuserservice.dto.request.TokenRefreshRequest;
import org.humber.authuserservice.dto.response.AuthResponse;
import org.humber.authuserservice.service.impl.AuthServiceImpl;
import org.humber.authuserservice.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<AuthResponse>> registerUser(@Valid @RequestBody RegisterRequest request) {
        return authService.registerUser(request)
                .thenApply(authResponse -> ResponseEntity.status(HttpStatus.CREATED).body(authResponse));
    }

    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<AuthResponse>> authenticateUser(@Valid @RequestBody LoginRequest request) {
        return authService.authenticateUser(request)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/refresh")
    public CompletableFuture<ResponseEntity<AuthResponse>> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        return refreshTokenService.processRefreshToken(request.refreshToken())
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "auth-user-service",
                "timestamp", java.time.Instant.now().toString()
        ));
    }
}