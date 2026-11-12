package com.fitnessApplication.controller;

import com.fitnessApplication.dto.response.Dashboard.DashboardResponse;
import com.fitnessApplication.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(
            Authentication authentication) {

        String email = authentication.getName();

        DashboardResponse response =
                dashboardService.getDashboard(email);

        return ResponseEntity.ok(response);
    }
}
