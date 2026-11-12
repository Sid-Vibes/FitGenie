package com.fitnessApplication.service;

import com.fitnessApplication.dto.response.Activity.ActivityResponse;
import com.fitnessApplication.dto.response.Activity.ActivityStatisticsResponse;
import com.fitnessApplication.exception.ResourceNotFoundException;
import com.fitnessApplication.model.Activity;
import com.fitnessApplication.model.User;
import com.fitnessApplication.repository.ActivityRepository;
import com.fitnessApplication.repository.UserRepository;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityStatisticsService {

     private final ActivityRepository activityRepository;
     private final UserRepository userRepository;

    public ActivityStatisticsService(ActivityRepository activityRepository, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    public ActivityStatisticsResponse getMyActivityStatistics(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Long userId = user.getId();

        // All-time statistics
        long totalWorkouts =
                activityRepository.countTotalWorkouts(userId);

        long totalDuration =
                activityRepository.getTotalExerciseDuration(userId);

        double totalCalories =
                activityRepository.getTotalCaloriesBurned(userId);

        double totalDistance =
                activityRepository.getTotalDistance(userId);

        // Start of the current week: Monday at 00:00
        LocalDate today = LocalDate.now();

        LocalDateTime startOfWeek = today
                .with(java.time.DayOfWeek.MONDAY)
                .atStartOfDay();

        LocalDateTime startOfNextWeek = today
                .with(java.time.DayOfWeek.MONDAY)
                .plusWeeks(1)
                .atStartOfDay();

        // Activities from Monday up to, but not including, next Monday
        long activitiesThisWeek =
                activityRepository.countByUserIdAndActivityDateBetween(
                        userId,
                        startOfWeek,
                        startOfNextWeek.minusNanos(1)
                );

        return ActivityStatisticsResponse.builder()
                .totalWorkouts(totalWorkouts)
                .totalExerciseDurationMinutes(totalDuration)
                .totalCaloriesBurned(totalCalories)
                .totalDistanceKm(totalDistance)
                .activitiesThisWeek(activitiesThisWeek)
                .build();
    }
    public List<ActivityResponse> getMyActivityHistory(
            String email,
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Start date and end date are required");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        LocalDateTime start = startDate.atStartOfDay();

        // Exclusive upper bound includes the entire end date
        LocalDateTime endExclusive = endDate.plusDays(1).atStartOfDay();

        List<Activity> activities =
                activityRepository
                        .findAllByUserIdAndActivityDateBetween(
                                user.getId(),
                                start,
                                endExclusive.minusNanos(1)
                        );

        return activities.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // MAP ENTITY TO RESPONSE DTO
    private ActivityResponse mapToResponse(Activity activity) {

        return ActivityResponse.builder()
                .id(activity.getId())
                .activityName(activity.getActivityName())
                .activityType(activity.getActivityType())
                .durationMinutes(activity.getDurationMinutes())
                .caloriesBurned(activity.getCaloriesBurned())
                .distanceKm(activity.getDistanceKm())
                .averageHeartRate(activity.getAverageHeartRate())
                .notes(activity.getNotes())
                .activityDate(activity.getActivityDate())
                .build();
    }

}


