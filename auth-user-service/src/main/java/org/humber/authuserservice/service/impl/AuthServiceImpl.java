package org.humber.authuserservice.service.impl;

import org.humber.authuserservice.dto.request.LoginRequest;
import org.humber.authuserservice.dto.request.RegisterRequest;
import org.humber.authuserservice.dto.response.AuthResponse;
import org.humber.authuserservice.entity.RefreshToken;
import org.humber.authuserservice.entity.Role;
import org.humber.authuserservice.entity.User;
import org.humber.authuserservice.exception.ResourceNotFoundException;
import org.humber.authuserservice.exception.UserAlreadyExistsException;
import org.humber.authuserservice.repository.UserRepository;
import org.humber.authuserservice.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.humber.authuserservice.service.AuthService;
import org.humber.authuserservice.service.RefreshTokenService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;


    // Using CompletableFuture for async programming


    // registration
    @Async("asyncExecutor")
    @Transactional
    @Override
    public CompletableFuture<AuthResponse> registerUser(RegisterRequest request) {
        String cleanEmail = request.email().toLowerCase().trim();
        log.info("Attempting to register user with email: {}", cleanEmail);
        if (userRepository.existsByEmailAndIsDeletedFalse(cleanEmail)) {
            log.warn("Registration failed. Email already in use: {}", cleanEmail);
            throw new UserAlreadyExistsException("Error: Email is already in use!");
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(cleanEmail)
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_USER) // USER by default
                .build();

        userRepository.save(user);
        userRepository.flush();
        log.info("User registered successfully: {}", cleanEmail);
        return CompletableFuture.completedFuture(performAuth(cleanEmail, request.password(), user));
    }

    // login wrapper
    @Async("asyncExecutor")
    @Transactional(readOnly = true)
    @Override
    public CompletableFuture<AuthResponse> authenticateUser(LoginRequest request) {
        String cleanEmail = request.email().toLowerCase().trim();
        log.info("Authenticating user: {}", cleanEmail);

        // User user = userRepository.findById(userId)
        //         .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User user = userRepository.findByEmailAndIsDeletedFalse(cleanEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));


        return CompletableFuture.completedFuture(performAuth(cleanEmail, request.password(), user));
    
        
    
    }

    // authentication
    private AuthResponse performAuth(String email, String password, User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        log.info("User logged in successfully: {}", email);
        return new AuthResponse(jwt, refreshToken.getToken(), user);
    }
}