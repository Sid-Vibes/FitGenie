package com.fitnessApplication.dto.request.Activity;

import com.fitnessApplication.model.Activity;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record ActivityCreateRequest(
        @NotBlank(message = "Activity name is required")
        @Size(max = 100)
        String activityName,

        @NotNull(message = "Activity type is required")
        Activity.ActivityType activityType,

        @NotNull(message = "Duration is required")
        @Positive(message = "Duration must be greater than zero")
        Integer durationMinutes,

        @PositiveOrZero(message = "Calories cannot be negative")
        Double caloriesBurned,

        @PositiveOrZero(message = "Distance cannot be negative")
        Double distanceKm,

        @Positive(message = "Heart rate must be positive")
        @Max(value = 250, message = "Heart rate is too high")
        Integer averageHeartRate,

        @NotNull(message = "Activity date is required")
        LocalDateTime activityDate,

        @Size(max = 500)
        String notes
) {
}
