package com.fitnessApplication.dto.response.Activity;

import com.fitnessApplication.model.Activity;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ActivityResponse(
        Long id,
        String activityName,
        Activity.ActivityType activityType,
        Integer durationMinutes,
        Double caloriesBurned,
        Double distanceKm,
        Integer averageHeartRate,
        LocalDateTime activityDate,
        String notes,
        Long userId
) {
}
