package com.fitnessApplication.dto.request.GoalRequest;


import com.fitnessApplication.model.Activity;
import com.fitnessApplication.model.Goal;
import com.fitnessApplication.model.RecommendationFocus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;


public record GoalCreateRequest(

        @NotBlank(message = "Goal title is required")
        String title,

        String description,

        @NotNull(message = "Goal type is required")
        Goal.GoalType goalType,

        @NotNull(message = "Activity type is required")
        Activity.ActivityType activityType,

        @NotNull(message = "Target value is required")
        @Positive(message = "Target value must be positive")
        Double targetValue,

        @NotBlank(message = "Unit is required")
        String unit,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate,

        RecommendationFocus recommendationFocus
) {

}
