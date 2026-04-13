package org.humber.authuserservice.service;

import org.humber.authuserservice.dto.response.AuthResponse;
import org.humber.authuserservice.entity.RefreshToken;
import org.humber.authuserservice.entity.User;

import java.util.concurrent.CompletableFuture;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    CompletableFuture<RefreshToken> verifyExpiration(RefreshToken token);
    CompletableFuture<AuthResponse> processRefreshToken(String requestRefreshToken);
}