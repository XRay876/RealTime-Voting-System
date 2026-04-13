package org.humber.authuserservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.humber.authuserservice.dto.request.PasswordUpdateRequest;
import org.humber.authuserservice.dto.request.ProfileUpdateRequest;
import org.humber.authuserservice.dto.response.UserResponse;
import org.humber.authuserservice.entity.Role;
import org.humber.authuserservice.entity.User;
import org.humber.authuserservice.exception.ResourceNotFoundException;
import org.humber.authuserservice.mapper.UserMapper;
import org.humber.authuserservice.repository.UserRepository;
import org.humber.authuserservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Async("asyncExecutor")
    @Override
    @Transactional(readOnly = true)
    public CompletableFuture<UserResponse> getCurrentUser(String email) {
        User user = getUserByEmail(email);
        return CompletableFuture.completedFuture(userMapper.toResponse(user));
    }

    @Async("asyncExecutor")
    @Transactional
    @Override
    public CompletableFuture<UserResponse> updateProfile(String email, ProfileUpdateRequest request) {
        User user = getUserByEmail(email);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        return CompletableFuture.completedFuture(userMapper.toResponse(userRepository.save(user)));
    }

    @Async("asyncExecutor")
    @Transactional
    @Override
    public CompletableFuture<Void> changePassword(String email, PasswordUpdateRequest request) {
        User user = getUserByEmail(email);
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        return CompletableFuture.completedFuture(null);
    }

    @Async("asyncExecutor")
    @Transactional
    @Override
    public CompletableFuture<Void> deleteUser(String email) {
        User user = getUserByEmail(email);
        user.setDeleted(true);
        userRepository.save(user);
        return CompletableFuture.completedFuture(null);
    }

    @Async("asyncExecutor")
    @Override
    @Transactional(readOnly = true)
    public CompletableFuture<UserResponse> getUserById(UUID id) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return CompletableFuture.completedFuture(userMapper.toResponse(user));
    }

    @Async("asyncExecutor")
    @Override
    @Transactional(readOnly = true)
    public CompletableFuture<Boolean> existsByEmail(String email) {
        return CompletableFuture.completedFuture(userRepository.existsByEmailAndIsDeletedFalse(email));
    }

    @Async("asyncExecutor")
    @Transactional
    @Override
    public CompletableFuture<UserResponse> updateRole(UUID userId, Role newRole) {
        log.info("Updating role for user {} to {}", userId, newRole);
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRole(newRole);
        return CompletableFuture.completedFuture(userMapper.toResponse(userRepository.save(user)));
    }

    @Async("asyncExecutor")
    @Transactional
    @Override
    public CompletableFuture<Void> promoteToAdmin(String email) {
        log.warn("USER SELF-PROMOTION TO ADMIN: {}", email);
        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRole(Role.ROLE_ADMIN);
        userRepository.save(user);
        return CompletableFuture.completedFuture(null);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}