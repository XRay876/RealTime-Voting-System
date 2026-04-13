package org.humber.authuserservice.service;

import org.humber.authuserservice.dto.request.LoginRequest;
import org.humber.authuserservice.dto.request.RegisterRequest;
import org.humber.authuserservice.dto.response.AuthResponse;
import org.humber.authuserservice.entity.User;

import java.util.concurrent.CompletableFuture;

public interface AuthService {

    CompletableFuture<AuthResponse> registerUser(RegisterRequest request);
    CompletableFuture<AuthResponse> authenticateUser(LoginRequest request);
}