package com.fitnessApplication.dto.response.User;

public record RefreshTokenResponse(

        String accessToken,
        String tokenType
) {
}
