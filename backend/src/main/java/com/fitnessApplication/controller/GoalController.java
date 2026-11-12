package com.fitnessApplication.controller;

import com.fitnessApplication.dto.request.GoalRequest.GoalCreateRequest;
import com.fitnessApplication.dto.response.Goal.GoalResponse;
import com.fitnessApplication.service.GoalService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Validated
public class GoalController {

    private final GoalService goalService;

    // 1. Create Goal
    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(
            Authentication authentication,
            @Valid @RequestBody GoalCreateRequest request
    ) {
        String email = authentication.getName();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(goalService.createGoal(email, request));
    }

    // 2. Get All Goals
    @GetMapping
    public ResponseEntity<List<GoalResponse>> getMyGoals(
            Authentication authentication
    ) {
        String email = authentication.getName();

        return ResponseEntity.ok(
                goalService.getMyGoals(email)
        );
    }

    // 3. Get Goal By ID
    @GetMapping("/{goalId}")
    public ResponseEntity<GoalResponse> getGoalById(
            Authentication authentication,
            @PathVariable Long goalId
    ) {
        String email = authentication.getName();

        return ResponseEntity.ok(
                goalService.getGoalById(email, goalId)
        );
    }

    // 4. Update Goal
    @PutMapping("/{goalId}")
    public ResponseEntity<GoalResponse> updateGoal(
            Authentication authentication,
            @PathVariable Long goalId,
            @Valid @RequestBody GoalCreateRequest request
    ) {
        String email = authentication.getName();

        return ResponseEntity.ok(
                goalService.updateGoal(email, goalId, request)
        );
    }

    // 5. Update Goal Progress
    @PatchMapping("/{goalId}/progress")
    public ResponseEntity<GoalResponse> updateGoalProgress(
            Authentication authentication,
            @PathVariable Long goalId,
            @RequestParam
            @NotNull
            @PositiveOrZero
            Double currentValue
    ) {
        String email = authentication.getName();

        return ResponseEntity.ok(
                goalService.updateGoalProgress(
                        email,
                        goalId,
                        currentValue
                )
        );
    }

    // 6. Delete Goal
    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(
            Authentication authentication,
            @PathVariable Long goalId
    ) {
        String email = authentication.getName();

        goalService.deleteGoal(email, goalId);

        return ResponseEntity.noContent().build();
    }
}

