package com.fitnessApplication.dto.request.Recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fitnessApplication.model.Recommendation;
import jakarta.validation.constraints.NotNull;


public record MLRecommendationFeedbackRequest(
        @JsonProperty("user_id")
        Long userId,

        String status

) {}