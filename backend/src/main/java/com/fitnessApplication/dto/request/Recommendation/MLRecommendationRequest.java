package com.fitnessApplication.dto.request.Recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MLRecommendationRequest(

        @JsonProperty("user_id")
        Long userId,

        Integer age,
        Double bmi,

        @JsonProperty("goal_progress_percentage")
        Double goalProgressPercentage,

        @JsonProperty("days_remaining")
        Integer daysRemaining,

        @JsonProperty("workouts_last_30_days")
        Integer workoutsLast30Days,

        @JsonProperty("avg_duration_minutes")
        Double avgDurationMinutes,

        @JsonProperty("avg_distance_km")
        Double avgDistanceKm,

        @JsonProperty("avg_calories_burned")
        Double avgCaloriesBurned,

        @JsonProperty("total_duration_minutes")
        Double totalDurationMinutes,

        @JsonProperty("total_distance_km")
        Double totalDistanceKm,

        @JsonProperty("days_since_last_activity")
        Integer daysSinceLastActivity,

        @JsonProperty("fitness_goal")
        String fitnessGoal,

        @JsonProperty("activity_level")
        String activityLevel,

        @JsonProperty("goal_type")
        String goalType,

        @JsonProperty("goal_activity_type")
        String goalActivityType,

        @JsonProperty("previous_activity_type")
        String previousActivityType

) {}