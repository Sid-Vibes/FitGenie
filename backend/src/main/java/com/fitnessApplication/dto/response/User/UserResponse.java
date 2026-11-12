package com.fitnessApplication.dto.response.User;

import com.fitnessApplication.model.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponse(
        Long id,
        String name,
        String email,
        Integer age,
        Double heightCm,
        Double weightKg,
        User.FitnessGoal fitnessGoal,
        User.ActivityLevel activityLevel,
        LocalDateTime createdAt

) {
}
