package com.fitnessApplication.dto.response.Dashboard;

import com.fitnessApplication.dto.response.Activity.ActivityResponse;
import com.fitnessApplication.dto.response.Activity.ActivityStatisticsResponse;
import lombok.Builder;

import java.util.List;
@Builder
public record DashboardResponse(
        UserSummary user,
        ActivityStatisticsResponse statistics,
        List<ActivityResponse> recentActivities
) {
    @Builder
    public record UserSummary(
            String name,
            String email,
            Double bmi
    )  {}
}
