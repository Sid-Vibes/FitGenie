package com.fitnessApplication.repository;

import com.fitnessApplication.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    // Get all goals belonging to a user
    List<Goal> findAllByUserId(Long userId);

    // Get all goals ordered by creation date
    List<Goal> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    // Get a particular goal belonging to a user
    Optional<Goal> findByIdAndUserId(Long goalId, Long userId);

    // Get goals by status
    List<Goal> findAllByUserIdAndStatus(
            Long userId,
            Goal.GoalStatus status
    );

    // Get active goals
    List<Goal> findAllByUserIdAndStatusIn(
            Long userId,
            List<Goal.GoalStatus> statuses
    );
}