package com.fitnessApplication.repository;

import com.fitnessApplication.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByUserId(Long userId);

    List<Activity> findByUserIdOrderByActivityDateDesc(Long userId);

    List<Activity> findAllByUserId(Long userId);

    // Find one activity belonging to a particular user
    Optional<Activity> findByIdAndUserId(Long id, Long userId);

    // Get activities within a date range
    List<Activity> findAllByUserIdAndActivityDateBetween(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
     // Get activities by userId and ActivityType
    List<Activity> findAllByUserIdAndActivityTypeAndActivityDateBetweenOrderByActivityDateDesc(
            Long userId,
            Activity.ActivityType activityType,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
    // 1. Total workouts
    @Query("""
        SELECT COUNT(a)
        FROM Activity a
        WHERE a.user.id = :userId
        """)
    long countTotalWorkouts(@Param("userId") Long userId);

    // 2. Total exercise duration
    @Query("""
        SELECT COALESCE(SUM(a.durationMinutes), 0)
        FROM Activity a
        WHERE a.user.id = :userId
        """)
    Long getTotalExerciseDuration(@Param("userId") Long userId);

    // 3. Total calories burned
    @Query("""
        SELECT COALESCE(SUM(a.caloriesBurned), 0.0)
        FROM Activity a
        WHERE a.user.id = :userId
        """)
    Double getTotalCaloriesBurned(@Param("userId") Long userId);

    // 4. Total distance travelled
    @Query("""
        SELECT COALESCE(SUM(a.distanceKm), 0.0)
        FROM Activity a
        WHERE a.user.id = :userId
        """)
    Double getTotalDistance(@Param("userId") Long userId);

    // 5. Number of activities within a date range
    long countByUserIdAndActivityDateBetween(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
