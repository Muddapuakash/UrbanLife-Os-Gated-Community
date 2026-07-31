package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.urbanlife.dto.*;
import com.urbanlife.service.StaffAttendanceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/staff-attendance")
public class StaffAttendanceController {

    private final StaffAttendanceService attendanceService;

    public StaffAttendanceController(
            StaffAttendanceService attendanceService) {

        this.attendanceService = attendanceService;
    }

    // =====================================================
    // RECORD STAFF ENTRY
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @PostMapping("/{staffId}/entry")
    public ResponseEntity<StaffAttendanceResponse> entry(
            @PathVariable Long staffId,
            @Valid @RequestBody StaffEntryRequest request) {

        return new ResponseEntity<>(
            attendanceService.recordEntry(
                staffId, request),
            HttpStatus.CREATED);
    }

    // =====================================================
    // RECORD STAFF EXIT
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @PatchMapping("/{staffId}/exit")
    public ResponseEntity<StaffAttendanceResponse> exit(
            @PathVariable Long staffId,
            @Valid @RequestBody StaffEntryRequest request) {

        return ResponseEntity.ok(
            attendanceService.recordExit(
                staffId, request));
    }

    // =====================================================
    // GET STAFF ATTENDANCE HISTORY
    // SUPER ADMIN + ADMIN + RESIDENT (own staff) + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY')"
    )
    @GetMapping("/{staffId}/history")
    public ResponseEntity<List<StaffAttendanceResponse>>
            history(@PathVariable Long staffId) {

        return ResponseEntity.ok(
            attendanceService
                .getStaffHistory(staffId));
    }

    // =====================================================
    // GET STAFF CURRENTLY INSIDE COMMUNITY
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping("/community/{communityId}/inside")
    public ResponseEntity<List<StaffAttendanceResponse>>
            currentlyInside(
                @PathVariable Long communityId) {

        return ResponseEntity.ok(
            attendanceService
                .getCurrentlyInside(communityId));
    }
}