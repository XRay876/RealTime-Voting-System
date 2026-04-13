package org.humber.authuserservice.service;

import org.humber.authuserservice.dto.request.PasswordUpdateRequest;
import org.humber.authuserservice.dto.request.ProfileUpdateRequest;
import org.humber.authuserservice.dto.response.UserResponse;
import org.humber.authuserservice.entity.Role;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserService {
    CompletableFuture<UserResponse> getCurrentUser(String email);
    CompletableFuture<UserResponse> updateProfile(String email, ProfileUpdateRequest request);
    CompletableFuture<Void> changePassword(String email, PasswordUpdateRequest request);
    CompletableFuture<Void> deleteUser(String email);
    CompletableFuture<UserResponse> getUserById(UUID id);
    CompletableFuture<Boolean> existsByEmail(String email);
    CompletableFuture<UserResponse> updateRole(UUID userId, Role newRole);
    CompletableFuture<Void> promoteToAdmin(String email);
}