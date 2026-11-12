package com.fitnessApplication.dto.request.Activity;

import com.fitnessApplication.model.Activity;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;

public record ActivityFilterRequest(

        Activity.ActivityType activityType,

        @PastOrPresent(message = "Start date cannot be in the future")
        LocalDateTime startDate,

        @PastOrPresent(message = "End date cannot be in the future")
        LocalDateTime endDate
) {
}
