package com.fitnessApplication.dto.response.User;

import lombok.Builder;

@Builder

public record UserAuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        UserResponse user
) {
}
