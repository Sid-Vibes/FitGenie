package com.fitnessApplication.service;

import com.fitnessApplication.dto.request.Activity.ActivityCreateRequest;
import com.fitnessApplication.dto.response.Activity.ActivityResponse;
import com.fitnessApplication.exception.ResourceNotFoundException;
import com.fitnessApplication.model.Activity;
import com.fitnessApplication.model.User;
import com.fitnessApplication.repository.ActivityRepository;
import com.fitnessApplication.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public ActivityService(
            ActivityRepository activityRepository,
            UserRepository userRepository) {

        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    // 1. CREATE ACTIVITY
    @Transactional
    public ActivityResponse createActivity(
            String email,
            ActivityCreateRequest request) {

        // Find the currently authenticated user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        // Create a new activity
        Activity activity = Activity.builder()
                .activityName(request.activityName())
                .activityType(request.activityType())
                .durationMinutes(request.durationMinutes())
                .caloriesBurned(request.caloriesBurned())
                .distanceKm(request.distanceKm())
                .averageHeartRate(request.averageHeartRate())
                .notes(request.notes())
                .activityDate(request.activityDate())
                .user(user)
                .build();

        // Save the activity
        Activity savedActivity = activityRepository.save(activity);

        return mapToResponse(savedActivity,user);
    }


    // 2. GET ALL ACTIVITIES OF LOGGED-IN USER
    @Transactional(readOnly = true)
    public List<ActivityResponse> getMyActivities(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        List<Activity> activities =
                activityRepository.findAllByUserId(user.getId());

        return activities.stream()
                .map((Activity activity) -> mapToResponse(activity,user))
                .toList();
    }


    // 3. GET ONE ACTIVITY BY ID
    @Transactional(readOnly = true)
    public ActivityResponse getMyActivityById(
            String email,
            Long activityId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Activity activity = activityRepository
                .findByIdAndUserId(activityId, user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Activity not found"));

        return mapToResponse(activity,user);
    }


    // 4. UPDATE ACTIVITY
    @Transactional
    public ActivityResponse updateActivity(
            String email,
            Long activityId,
            ActivityCreateRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        // Verify that this activity belongs to the user
        Activity activity = activityRepository
                .findByIdAndUserId(activityId, user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Activity not found"));

        // Update the permitted fields
        activity.setActivityName(request.activityName());
        activity.setActivityType(request.activityType());
        activity.setDurationMinutes(request.durationMinutes());
        activity.setCaloriesBurned(request.caloriesBurned());
        activity.setDistanceKm(request.distanceKm());
        activity.setAverageHeartRate(request.averageHeartRate());
        activity.setNotes(request.notes());
        activity.setActivityDate(request.activityDate());

        Activity updatedActivity =
                activityRepository.save(activity);

        return mapToResponse(updatedActivity,user);
    }


    // 5. DELETE ACTIVITY
    @Transactional
    public void deleteActivity(
            String email,
            Long activityId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        // Verify ownership before deleting
        Activity activity = activityRepository
                .findByIdAndUserId(activityId, user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Activity not found"));

        activityRepository.delete(activity);
    }


    // 6. GET ACTIVITIES WITHIN A DATE RANGE
    @Transactional(readOnly = true)
    public List<ActivityResponse> getMyActivitiesByDateRange(
            String email,
            LocalDateTime startDate,
            LocalDateTime endDate) {

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

        List<Activity> activities =
                activityRepository
                        .findAllByUserIdAndActivityDateBetween(
                                user.getId(),
                                startDate,
                                endDate
                        );

        return activities.stream()
                .map((Activity activity) -> mapToResponse(activity,user))
                .toList();
    }


    // MAP ENTITY TO RESPONSE DTO
    private ActivityResponse mapToResponse(Activity activity,User user) {

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
                .userId(user.getId())
                .build();
    }
}

