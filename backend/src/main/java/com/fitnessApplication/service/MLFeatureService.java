package com.fitnessApplication.service;

import com.fitnessApplication.dto.request.Recommendation.MLRecommendationRequest;
import com.fitnessApplication.model.Activity;
import com.fitnessApplication.model.Goal;
import com.fitnessApplication.model.User;
import com.fitnessApplication.repository.ActivityRepository;
import com.fitnessApplication.repository.GoalRepository;
import com.fitnessApplication.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MLFeatureService {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final ActivityRepository activityRepository;

    public MLRecommendationRequest collectFeatures(String email) {

        // 1. Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Long userId = user.getId();

        // 2. Fetch active goals
        List<Goal> goals =
                goalRepository.findAllByUserIdAndStatusIn(
                        userId,
                        List.of(
                                Goal.GoalStatus.IN_PROGRESS,
                                Goal.GoalStatus.NOT_STARTED
                        )
                );

        Goal goal = goals.stream()
                .filter(g -> g.getRecommendationFocus() != null)
                .filter(g -> g.getActivityType() != null)
                .filter(g -> g.getEndDate() != null)
                .sorted((a, b) ->
                        b.getCreatedAt().compareTo(a.getCreatedAt()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No suitable active goal found for ML recommendation"
                        ));

        // 3. Fetch all activities (newest first)
        List<Activity> activities =
                activityRepository.findByUserIdOrderByActivityDateDesc(
                        userId
                );

        // 4. Calculate BMI
        double bmi = calculateBMI(user);

        // 5. Calculate goal progress
        double goalProgress =
                (goal.getCurrentValue() / goal.getTargetValue()) * 100.0;

        goalProgress = Math.min(100.0,
                Math.max(0.0, goalProgress));

        // 6. Calculate remaining days
        int daysRemaining = (int) Math.max(
                0,
                ChronoUnit.DAYS.between(
                        LocalDate.now(),
                        goal.getEndDate()
                )
        );

        // 7. Last 30 days activities
        LocalDateTime thirtyDaysAgo =
                LocalDateTime.now().minusDays(30);

        long workoutsLast30Days = activities.stream()
                .filter(a -> !a.getActivityDate()
                        .isBefore(thirtyDaysAgo))
                .count();

        // 8. Average duration
        double avgDuration = activities.stream()
                .mapToInt(Activity::getDurationMinutes)
                .average()
                .orElse(0.0);

        // 9. Average distance (ignore null values)
        double avgDistance = activities.stream()
                .map(Activity::getDistanceKm)
                .filter(d -> d != null)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        // 10. Average calories (ignore null values)
        double avgCalories = activities.stream()
                .map(Activity::getCaloriesBurned)
                .filter(c -> c != null)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        // 11. Total duration
        double totalDuration = activities.stream()
                .mapToDouble(Activity::getDurationMinutes)
                .sum();

        // 12. Total distance
        double totalDistance = activities.stream()
                .map(Activity::getDistanceKm)
                .filter(d -> d != null)
                .mapToDouble(Double::doubleValue)
                .sum();

        // 13. Days since last activity
        int daysSinceLastActivity = activities.isEmpty()
                ? 30
                : (int) ChronoUnit.DAYS.between(
                activities.get(0).getActivityDate().toLocalDate(),
                LocalDate.now()
        );

        // 14. Previous activity type
        String previousActivityType = activities.isEmpty()
                ? "OTHER"
                : activities.get(0).getActivityType().name();

        // 15. Build ML request
        return new MLRecommendationRequest(
                userId,
                user.getAge(),
                bmi,
                goalProgress,
                daysRemaining,
                (int) workoutsLast30Days,
                avgDuration,
                avgDistance,
                avgCalories,
                totalDuration,
                totalDistance,
                daysSinceLastActivity,
                user.getFitnessGoal().name(),
                user.getActivityLevel().name(),
                goal.getRecommendationFocus().name(),
                goal.getActivityType().name(),
                previousActivityType
        );
    }

    private double calculateBMI(User user) {

        if (user.getHeightCm() == null ||
                user.getWeightKg() == null ||
                user.getHeightCm() <= 0) {

            throw new IllegalStateException(
                    "Valid height and weight are required to calculate BMI"
            );
        }

        double heightMeters = user.getHeightCm() / 100.0;

        return user.getWeightKg() /
                (heightMeters * heightMeters);
    }
}