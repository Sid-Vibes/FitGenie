package com.fitnessApplication.repository;

import com.fitnessApplication.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation,Long> {
    List<Recommendation> findByUserId(Long userId);

    List<Recommendation> findByUserIdAndStatus(
            Long userId,
            Recommendation.RecommendationStatus status
    );

    List<Recommendation> findByUserIdOrderByCreatedAtDesc(
            Long userId
    );

    List<Recommendation> findByUserIdAndRelatedGoalId(
            Long userId,
            Long goalId
    );

    List<Recommendation> findByUserIdAndRelatedGoalIdAndStatus(
            Long userId,
            Long goalId,
            Recommendation.RecommendationStatus status
    );
    /* aviod duplication */
    boolean existsByUserIdAndRelatedGoalIdAndRecommendationTypeAndStatus(
            Long userId,
            Long goalId,
            Recommendation.RecommendationType recommendationType,
            Recommendation.RecommendationStatus status
    );
}
