package org.humber.authuserservice.service.impl;

import org.humber.authuserservice.dto.response.AuthResponse;
import org.humber.authuserservice.entity.RefreshToken;
import org.humber.authuserservice.entity.User;
import org.humber.authuserservice.exception.ResourceNotFoundException;
import org.humber.authuserservice.exception.TokenRefreshException;
import org.humber.authuserservice.repository.RefreshTokenRepository;
import org.humber.authuserservice.repository.UserRepository;
import org.humber.authuserservice.security.jwt.JwtUtils;
import org.humber.authuserservice.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;


    // creating refresh token which we gonna save in db
    @Transactional
    @Override
    public RefreshToken createRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);



        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }


    // checking if token is expired (7 days)
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<RefreshToken> verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new sign in request");
        }


        return CompletableFuture.completedFuture(token);
    }

    @Async("asyncExecutor")
    @Transactional(readOnly = true)
    @Override
    public CompletableFuture<AuthResponse> processRefreshToken(String requestRefreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));

        verifyExpiration(token).join();


        User user = token.getUser();

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String newAccessToken = jwtUtils.generateJwtToken(auth);

        return CompletableFuture.completedFuture(new AuthResponse(newAccessToken, requestRefreshToken, user));
    }
}