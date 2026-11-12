package com.fitnessApplication.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Activity name is required")
    @Column(nullable = false, length = 100)
    private String activityName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType activityType;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    @Column(nullable = false)
    private Integer durationMinutes;

    @PositiveOrZero(message = "Calories cannot be negative")
    private Double caloriesBurned;

    @PositiveOrZero(message = "Distance cannot be negative")
    private Double distanceKm;

    @Positive(message = "Heart rate must be positive")
    private Integer averageHeartRate;

    @NotNull(message = "Activity date is required")
    @Column(nullable = false)
    private LocalDateTime activityDate;

    @Column(length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public enum ActivityType {
        WALKING,
        RUNNING,
        CYCLING,
        SWIMMING,
        WEIGHT_TRAINING,
        YOGA,
        OTHER
    }
}
