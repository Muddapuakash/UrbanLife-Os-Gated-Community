package com.urbanlife.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.urbanlife.dto.DashboardResponse;
import com.urbanlife.service.DashboardService;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService) {

        this.dashboardService =
                dashboardService;
    }

    // =====================================================
    // GET COMMUNITY DASHBOARD
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/community/{communityId}")
    public ResponseEntity<DashboardResponse>
            getCommunityDashboard(
                    @PathVariable Long communityId) {

        return ResponseEntity.ok(
                dashboardService
                    .getCommunityDashboard(
                        communityId));
    }
}