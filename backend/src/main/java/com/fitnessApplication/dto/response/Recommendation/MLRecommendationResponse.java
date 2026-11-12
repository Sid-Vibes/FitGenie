package com.fitnessApplication.dto.response.Recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MLRecommendationResponse(

        UUID id,

        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("activity_type")
        String activityType,

        String intensity,

        @JsonProperty("duration_minutes")
        Integer durationMinutes,

        @JsonProperty("distance_km")
        Double distanceKm,

        String explanation,

        String status,

        @JsonProperty("model_version")
        String modelVersion,

        @JsonProperty("created_at")
        OffsetDateTime createdAt

) {}
