package com.fitnessApplication.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Goal title is required")
    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalType goalType;

    @NotNull(message = "Target value is required")
    @Positive(message = "Target value must be positive")
    @Column(nullable = false)
    private Double targetValue;

    @PositiveOrZero(message = "Current value cannot be negative")
    @Builder.Default
    @Column(nullable = false)
    private Double currentValue = 0.0;

    @NotBlank(message = "Unit is required")
    @Column(nullable = false, length = 20)
    private String unit;

    @NotNull(message = "Start date is required")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private GoalStatus status = GoalStatus.NOT_STARTED;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Multiple goals can belong to one user
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private Activity.ActivityType activityType;

    public enum GoalType {
        WEIGHT_LOSS,
        WEIGHT_GAIN,
        CALORIES_BURNED,
        WORKOUT_COUNT,
        DISTANCE,
        EXERCISE_DURATION
    }

    public enum GoalStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
    public enum ActivityType {
        WALKING,
        RUNNING,
        CYCLING,
        SWIMMING,
        WEIGHT_TRAINING,
        YOGA,
        OTHER
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation_focus")
    private RecommendationFocus recommendationFocus;

}