package com.fitnessApplication.service;


import com.fitnessApplication.model.RefreshToken;
import com.fitnessApplication.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }
     @Transactional
    public String createRefreshToken(String email) {
        // Revoke existing tokens
        RefreshToken refreshToken = refreshTokenRepository.findByEmail(email)
                .orElse(new RefreshToken());
        refreshToken.setEmail(email);
        refreshToken.setToken(UUID.randomUUID().toString());
        long sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000L; // 7 days in milliseconds
        refreshToken.setExpiryDate(new Date(System.currentTimeMillis() + sevenDaysInMillis));
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();

    }
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElse(null);
        if (refreshToken==null) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        Date currentDate = new Date();
        if (refreshToken.getExpiryDate().before(currentDate)) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("Refresh token expired");
        }
        return refreshToken;
    }

}