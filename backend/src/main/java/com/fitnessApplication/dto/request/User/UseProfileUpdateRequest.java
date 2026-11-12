package com.fitnessApplication.dto.request.User;

import com.fitnessApplication.model.User;
import jakarta.validation.constraints.*;


public record UseProfileUpdateRequest(
        @NotBlank(message = "email is required")
        @Size(max = 100, message = "email cannot exceed 100 characters")
        String email,

        @NotNull(message = "Height is required")
        @Positive(message = "Height must be positive")
        @DecimalMax(value = "300.0", message = "Height is too high")
        Double heightCm,

        @NotNull(message = "Weight is required")
        @Positive(message = "Weight must be positive")
        @DecimalMax(value = "700.0", message = "Weight is too high")
        Double weightKg,

        @NotNull(message = "Fitness goal is required")
        User.FitnessGoal fitnessGoal,

        @NotNull(message = "Activity level is required")
        User.ActivityLevel activityLevel
) {
}
