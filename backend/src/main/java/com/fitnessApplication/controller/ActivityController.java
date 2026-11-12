package com.fitnessApplication.controller;

import com.fitnessApplication.dto.request.Activity.ActivityCreateRequest;
import com.fitnessApplication.dto.response.Activity.ActivityResponse;
import com.fitnessApplication.dto.response.Activity.ActivityStatisticsResponse;
import com.fitnessApplication.service.ActivityService;

import com.fitnessApplication.service.ActivityStatisticsService;
import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final ActivityStatisticsService activityStatisticsService;

    public ActivityController(ActivityService activityService, ActivityStatisticsService activityStatisticsService) {
        this.activityService = activityService;
        this.activityStatisticsService= activityStatisticsService;
    }

    // 1. Create a new activity
    @PostMapping
    public ResponseEntity<ActivityResponse> createActivity(
            @Valid @RequestBody ActivityCreateRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        ActivityResponse response =
                activityService.createActivity(email, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 2. Get all activities
    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getMyActivities(
            Authentication authentication) {

        String email = authentication.getName();

        List<ActivityResponse> activities =
                activityService.getMyActivities(email);

        return ResponseEntity.ok(activities);
    }

    // 3. Get one activity by ID
    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponse> getMyActivityById(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        ActivityResponse response =
                activityService.getMyActivityById(email, id);

        return ResponseEntity.ok(response);
    }

    // 4. Update an existing activity
    @PutMapping("/{id}")
    public ResponseEntity<ActivityResponse> updateActivity(
            @PathVariable Long id,
            @Valid @RequestBody ActivityCreateRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        ActivityResponse response =
                activityService.updateActivity(email, id, request);

        return ResponseEntity.ok(response);
    }

    // 5. Delete an activity
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        activityService.deleteActivity(email, id);

        return ResponseEntity.noContent().build();
    }

    // 6. Get activities between two dates
    @GetMapping("/range")
    public ResponseEntity<List<ActivityResponse>> getMyActivitiesByDateRange(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            Authentication authentication) {

        String email = authentication.getName();

        List<ActivityResponse> activities =
                activityService.getMyActivitiesByDateRange(
                        email, startDate, endDate);

        return ResponseEntity.ok(activities);
    }
    // 7. get all Statistics data of User
    @GetMapping("/statistics")
    public ResponseEntity<ActivityStatisticsResponse> getMyActivityStatistics(
            Authentication authentication) {

        String email = authentication.getName();

        ActivityStatisticsResponse response =
                activityStatisticsService.getMyActivityStatistics(email);

        return ResponseEntity.ok(response);
    }

    // 8. Get User History
    @GetMapping("/history")
    public ResponseEntity<List<ActivityResponse>> getMyActivityHistory(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            Authentication authentication) {

        String email = authentication.getName();

        List<ActivityResponse> history =
                activityStatisticsService.getMyActivityHistory(
                        email, startDate, endDate);

        return ResponseEntity.ok(history);
    }
}
