package com.fitnessApplication.dto.response.Activity;


import lombok.Builder;

@Builder
public record ActivityStatisticsResponse(
        long totalWorkouts,
        long totalExerciseDurationMinutes,
        double totalCaloriesBurned,
        double totalDistanceKm,
        long activitiesThisWeek
) {
}
