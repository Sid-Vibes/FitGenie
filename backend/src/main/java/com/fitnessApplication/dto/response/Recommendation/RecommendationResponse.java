package com.fitnessApplication.dto.response.Recommendation;

import com.fitnessApplication.model.Recommendation;
import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record RecommendationResponse(

        Long id,
        String title,
        String description,
        Recommendation.RecommendationType recommendationType,
        Recommendation.Priority priority,
        Recommendation.RecommendationStatus status,
        Long relatedGoalId,
        String relatedGoalTitle,
        Long relatedActivityId,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {
}
