package com.fitnessApplication.service;

import com.fitnessApplication.dto.request.Recommendation.MLRecommendationFeedbackRequest;
import com.fitnessApplication.dto.request.Recommendation.MLRecommendationRequest;
import com.fitnessApplication.dto.response.Recommendation.MLRecommendationResponse;
import com.fitnessApplication.model.User;
import com.fitnessApplication.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.UUID;


@Service
public class MLRecommendationService {

    private final UserRepository userRepository;
    private final WebClient mlWebClient;
    private final MLFeatureService mlFeatureService;
    public MLRecommendationService(WebClient mlWebClient,MLFeatureService mlFeatureService,UserRepository userRepository) {
        this.mlWebClient = mlWebClient;
        this.mlFeatureService=mlFeatureService;
        this.userRepository=userRepository;
    }
    public MLRecommendationResponse generateForUser(String email) {

        // Collect all 16 features from MySQL
        MLRecommendationRequest request =
                mlFeatureService.collectFeatures(email);

        // Send features to FastAPI and get prediction
        return generateRecommendation(request);
    }
    public MLRecommendationResponse generateRecommendation
            (MLRecommendationRequest request) {
        try {
            return mlWebClient.post()
                    .uri("/api/ml/recommendations")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(MLRecommendationResponse.class)
                    .block();

        } catch (WebClientResponseException ex) {

            throw new RuntimeException(
                    "ML Service Error: " + ex.getResponseBodyAsString(), ex
            );
        }
    }

    public MLRecommendationResponse submitFeedback(
            String email,
            UUID recommendationId,
            String status
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MLRecommendationFeedbackRequest request =
                new MLRecommendationFeedbackRequest(
                        user.getId(),
                        status
                );

        try {
            return mlWebClient.patch()
                    .uri("/api/ml/recommendations/{recommendationId}/feedback",
                            recommendationId)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(MLRecommendationResponse.class)
                    .block();

        } catch (WebClientResponseException ex) {
            throw new RuntimeException(
                    "ML Feedback Service Error: " + ex.getResponseBodyAsString(),
                    ex
            );
        }
    }

    public List<MLRecommendationResponse> getRecommendationHistory(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            return mlWebClient.get()
                    .uri("/api/ml/recommendations/user/{userId}", user.getId())
                    .retrieve()
                    .bodyToFlux(MLRecommendationResponse.class)
                    .collectList()
                    .block();

        } catch (WebClientResponseException ex) {
            throw new RuntimeException(
                    "ML History Service Error: " + ex.getResponseBodyAsString(),
                    ex
            );
        }
    }
}