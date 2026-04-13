package org.humber.authuserservice.controller;

import org.humber.authuserservice.dto.request.PasswordUpdateRequest;
import org.humber.authuserservice.dto.request.ProfileUpdateRequest;
import org.humber.authuserservice.dto.response.UserResponse;
import org.humber.authuserservice.entity.Role;
import org.humber.authuserservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/api/v1/users/me")
    public CompletableFuture<ResponseEntity<UserResponse>> getCurrentUser(Authentication authentication) {
        return userService.getCurrentUser(authentication.getName())
                .thenApply(ResponseEntity::ok);
    }

    @PutMapping("/api/v1/users/me")
    public CompletableFuture<ResponseEntity<UserResponse>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody ProfileUpdateRequest request) {
        return userService.updateProfile(authentication.getName(), request)
                .thenApply(ResponseEntity::ok);
    }

    @PatchMapping("/api/v1/admin/users/{id}/role")
    public CompletableFuture<ResponseEntity<UserResponse>> changeUserRole(
            @PathVariable UUID id,
            @RequestParam Role role) {
        return userService.updateRole(id, role)
                .thenApply(ResponseEntity::ok);
    }

    @PatchMapping("/api/v1/users/me/password")
    public CompletableFuture<ResponseEntity<Void>> changePassword(
            Authentication authentication,
            @Valid @RequestBody PasswordUpdateRequest request) {
        return userService.changePassword(authentication.getName(), request)
                .thenApply(v -> ResponseEntity.noContent().build());
    }

    @DeleteMapping("/api/v1/users/me")
    public CompletableFuture<ResponseEntity<Void>> deleteUser(Authentication authentication) {
        return userService.deleteUser(authentication.getName())
                .thenApply(v -> ResponseEntity.noContent().build());
    }

    @PostMapping("/api/v1/users/promote-me")
    public CompletableFuture<ResponseEntity<String>> promoteMe(Authentication authentication) {
        return userService.promoteToAdmin(authentication.getName())
                .thenApply(v -> ResponseEntity.ok("You are now an ADMIN. Please re-login to update your token."));
    }

    @GetMapping("/api/v1/admin/users/{id}")
    public CompletableFuture<ResponseEntity<UserResponse>> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/api/v1/admin/users/exists/{email}")
    public CompletableFuture<ResponseEntity<Boolean>> checkUserExists(@PathVariable String email) {
        return userService.existsByEmail(email)
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