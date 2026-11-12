package com.fitnessApplication.service;

import com.fitnessApplication.dto.response.Activity.ActivityResponse;
import com.fitnessApplication.dto.response.Activity.ActivityStatisticsResponse;
import com.fitnessApplication.dto.response.Dashboard.DashboardResponse;
import com.fitnessApplication.exception.ResourceNotFoundException;
import com.fitnessApplication.model.User;
import com.fitnessApplication.repository.ActivityRepository;
import com.fitnessApplication.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    public DashboardService(
            UserRepository userRepository,
            ActivityRepository activityRepository) {

        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(String email) {

        // 1. Get currently logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Long userId = user.getId();

        // 2. Calculate total statistics
        long totalWorkouts =
                activityRepository.countTotalWorkouts(userId);

        Long totalDuration =
                activityRepository.getTotalExerciseDuration(userId);

        Double totalCalories =
                activityRepository.getTotalCaloriesBurned(userId);

        Double totalDistance =
                activityRepository.getTotalDistance(userId);

        // 3. Calculate activities this week (Monday to Sunday)
        LocalDate today = LocalDate.now();

        LocalDate startOfWeek =
                today.with(DayOfWeek.MONDAY);

        LocalDate endOfWeek =
                startOfWeek.plusDays(6);

        LocalDateTime startDate =
                startOfWeek.atStartOfDay();

        LocalDateTime endDate =
                endOfWeek.atTime(LocalTime.MAX);

        long activitiesThisWeek =
                activityRepository.countByUserIdAndActivityDateBetween(
                        userId,
                        startDate,
                        endDate
                );

        // 4. Create statistics response
        ActivityStatisticsResponse statistics =
                ActivityStatisticsResponse.builder()
                        .totalWorkouts(totalWorkouts)
                        .totalExerciseDurationMinutes(totalDuration)
                        .totalCaloriesBurned(totalCalories)
                        .totalDistanceKm(totalDistance)
                        .activitiesThisWeek(activitiesThisWeek)
                        .build();

        // 5. Get  most recent activities
        List<ActivityResponse> recentActivities =
                activityRepository
                        .findByUserIdOrderByActivityDateDesc(userId)
                        .stream()
                        .limit(3)
                        .map(activity -> ActivityResponse.builder()
                                .id(activity.getId())
                                .activityName(activity.getActivityName())
                                .activityType(activity.getActivityType())
                                .durationMinutes(activity.getDurationMinutes())
                                .caloriesBurned(activity.getCaloriesBurned())
                                .distanceKm(activity.getDistanceKm())
                                .averageHeartRate(activity.getAverageHeartRate())
                                .notes(activity.getNotes())
                                .activityDate(activity.getActivityDate())
                                .userId(userId)
                                .build())
                        .toList();

        // 6. Calculate BMI
        Double bmi = calculateBMI(user);

        // 7. Return complete dashboard
        return DashboardResponse.builder()
                .user(DashboardResponse.UserSummary.builder()
                        .name(user.getName())
                        .email(user.getEmail())
                        .bmi(bmi)
                        .build())
                .statistics(statistics)
                .recentActivities(recentActivities)
                .build();
    }

    private Double calculateBMI(User user) {

        if (user.getHeightCm() == null ||
                user.getWeightKg() == null ||
                user.getHeightCm() <= 0) {
            return null;
        }

        double heightInMeters = user.getHeightCm() / 100.0;

        return Math.round(
                (user.getWeightKg() /
                        (heightInMeters * heightInMeters)) * 100.0
        ) / 100.0;
    }
}