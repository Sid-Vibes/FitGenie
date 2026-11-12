package com.fitnessApplication.dto.request.User;

import com.fitnessApplication.model.User;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record UserRegistrationRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email")
        @Size(max = 150)
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72,
                message = "Password must contain between 8 and 72 characters")
        String password,

        @Min(value = 13, message = "Age must be at least 13")
        @Max(value = 120, message = "Age cannot exceed 120")
        Integer age,

        @Positive(message = "Height must be positive")
        Double heightCm,

        @Positive(message = "Weight must be positive")
        Double weightKg,

        User.FitnessGoal fitnessGoal,

        User.ActivityLevel activityLevel
) {
}
