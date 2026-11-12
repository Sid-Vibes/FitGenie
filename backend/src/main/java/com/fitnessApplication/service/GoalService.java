package com.fitnessApplication.service;

import com.fitnessApplication.dto.request.GoalRequest.GoalCreateRequest;
import com.fitnessApplication.dto.response.Goal.GoalResponse;
import com.fitnessApplication.exception.ResourceNotFoundException;
import com.fitnessApplication.model.Goal;
import com.fitnessApplication.model.User;
import com.fitnessApplication.repository.GoalRepository;
import com.fitnessApplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    // 1. Create Goal
    @Transactional
    public GoalResponse createGoal(String email, GoalCreateRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        validateDates(request);

        Goal goal = Goal.builder()
                .title(request.title())
                .description(request.description())
                .goalType(request.goalType())
                .targetValue(request.targetValue())
                .currentValue(0.0)
                .unit(request.unit())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .status(Goal.GoalStatus.NOT_STARTED)
                .recommendationFocus(request.recommendationFocus())
                .user(user)
                .build();
        goal.setActivityType(request.activityType());
        Goal savedGoal = goalRepository.save(goal);

        return mapToResponse(savedGoal);
    }

    // 2. Get All Goals of Logged-in User
    @Transactional(readOnly = true)
    public List<GoalResponse> getMyGoals(String email) {

        User user = getUser(email);

        return goalRepository
                .findAllByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 3. Get Goal By ID
    @Transactional(readOnly = true)
    public GoalResponse getGoalById(String email, Long goalId) {

        User user = getUser(email);

        Goal goal = goalRepository
                .findByIdAndUserId(goalId, user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Goal not found"));

        return mapToResponse(goal);
    }

    // 4. Update Goal Details
    @Transactional
    public GoalResponse updateGoal(
            String email,
            Long goalId,
            GoalCreateRequest request
    ) {

        User user = getUser(email);

        Goal goal = goalRepository
                .findByIdAndUserId(goalId, user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Goal not found"));

        validateDates(request);

        goal.setTitle(request.title());
        goal.setDescription(request.description());
        goal.setGoalType(request.goalType());
        goal.setTargetValue(request.targetValue());
        goal.setUnit(request.unit());
        goal.setStartDate(request.startDate());
        goal.setActivityType(request.activityType());
        goal.setEndDate(request.endDate());

        // Preserve existing progress and recalculate status.
        updateStatus(goal);

        return mapToResponse(goalRepository.save(goal));
    }

    // 5. Update Goal Progress
    @Transactional
    public GoalResponse updateGoalProgress(
            String email,
            Long goalId,
            Double currentValue
    ) {

        User user = getUser(email);

        Goal goal = goalRepository
                .findByIdAndUserId(goalId, user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Goal not found"));

        if (currentValue == null || currentValue < 0) {
            throw new IllegalArgumentException(
                    "Current progress cannot be negative or null"
            );
        }

        if (goal.getStatus() == Goal.GoalStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cannot update progress of a cancelled goal"
            );
        }

        goal.setCurrentValue(currentValue);

        updateStatus(goal);

        return mapToResponse(goalRepository.save(goal));
    }

    // 6. Delete Goal
    @Transactional
    public void deleteGoal(String email, Long goalId) {

        User user = getUser(email);

        Goal goal = goalRepository
                .findByIdAndUserId(goalId, user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Goal not found"));

        goalRepository.delete(goal);
    }

    // Helper: Get User
    private User getUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    // Helper: Validate Dates
    private void validateDates(GoalCreateRequest request) {

        if (request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date"
            );
        }
    }

    // Helper: Update Goal Status
    private void updateStatus(Goal goal) {

        if (goal.getCurrentValue() >= goal.getTargetValue()) {
            goal.setStatus(Goal.GoalStatus.COMPLETED);
        } else if (goal.getCurrentValue() > 0) {
            goal.setStatus(Goal.GoalStatus.IN_PROGRESS);
        } else {
            goal.setStatus(Goal.GoalStatus.NOT_STARTED);
        }
    }

    // Helper: Convert Entity to Response DTO
    private GoalResponse mapToResponse(Goal goal) {

        double percentage =
                (goal.getCurrentValue() / goal.getTargetValue()) * 100;

        percentage = Math.min(percentage, 100.0);

        percentage = Math.round(percentage * 100.0) / 100.0;

        return GoalResponse.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .description(goal.getDescription())
                .goalType(goal.getGoalType())
                .targetValue(goal.getTargetValue())
                .currentValue(goal.getCurrentValue())
                .unit(goal.getUnit())
                .startDate(goal.getStartDate())
                .endDate(goal.getEndDate())
                .status(goal.getStatus())
                .progressPercentage(percentage)
                .createdAt(goal.getCreatedAt())
                .activityType(goal.getActivityType())
                .build();
    }
}