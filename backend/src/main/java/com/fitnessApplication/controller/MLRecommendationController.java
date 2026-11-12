package com.fitnessApplication.controller;

import com.fitnessApplication.dto.response.Recommendation.MLRecommendationResponse;
import com.fitnessApplication.service.MLRecommendationService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ml/recommendations")
@RequiredArgsConstructor
public class MLRecommendationController {

    private final MLRecommendationService mlRecommendationService;

    @PostMapping("/generate")
    public ResponseEntity<MLRecommendationResponse> generateRecommendation(
            Authentication authentication) {

        // Get email of currently authenticated user
        String email = authentication.getName();

        // Generate personalized recommendation
        MLRecommendationResponse response =
                mlRecommendationService.generateForUser(email);

        return ResponseEntity.ok(response);
    }
    @PatchMapping("/{recommendationId}/feedback")
    public ResponseEntity<MLRecommendationResponse> submitFeedback(
            @PathVariable UUID recommendationId,
            @RequestParam String status,
            Authentication authentication
    ) {
        String email = authentication.getName();

        MLRecommendationResponse response =
                mlRecommendationService.submitFeedback(
                        email,
                        recommendationId,
                        status
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<MLRecommendationResponse>> getRecommendationHistory(
            Authentication authentication
    ) {
        String email = authentication.getName();

        List<MLRecommendationResponse> history =
                mlRecommendationService.getRecommendationHistory(email);

        return ResponseEntity.ok(history);
    }
}