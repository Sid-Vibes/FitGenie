package com.fitnessApplication.dto.response.Goal;

import com.fitnessApplication.model.Activity;
import com.fitnessApplication.model.Goal;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record GoalResponse(
        Long id,
        String title,
        String description,
        Goal.GoalType goalType,
        Double targetValue,
        Double currentValue,
        String unit,
        LocalDate startDate,
        LocalDate endDate,
        Goal.GoalStatus status,
        Double progressPercentage,
        Activity.ActivityType activityType,
        LocalDateTime createdAt

) {}