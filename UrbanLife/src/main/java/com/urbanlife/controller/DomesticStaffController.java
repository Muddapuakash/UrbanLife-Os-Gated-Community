package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.urbanlife.dto.*;
import com.urbanlife.enums.*;
import com.urbanlife.service.DomesticStaffService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/staff")
public class DomesticStaffController {

    private final DomesticStaffService staffService;

    public DomesticStaffController(
            DomesticStaffService staffService) {

        this.staffService = staffService;
    }

    // =====================================================
    // REGISTER STAFF
    // SUPER ADMIN + ADMIN + RESIDENT
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @PostMapping
    public ResponseEntity<DomesticStaffResponse> create(
            @Valid @RequestBody
            CreateDomesticStaffRequest request) {

        return new ResponseEntity<>(
            staffService.createStaff(request),
            HttpStatus.CREATED);
    }

    // =====================================================
    // GET STAFF BY ID
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/{staffId}")
    public ResponseEntity<DomesticStaffResponse>
            getById(@PathVariable Long staffId) {

        return ResponseEntity.ok(
            staffService.getStaffById(staffId));
    }

    // =====================================================
    // GET STAFF BY COMMUNITY
    // SUPER ADMIN + ADMIN + RESIDENT + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY')"
    )
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<DomesticStaffResponse>>
            getByCommunity(
                @PathVariable Long communityId) {

        return ResponseEntity.ok(
            staffService.getByCommunity(communityId));
    }

    // =====================================================
    // GET STAFF BY TYPE
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping("/community/{communityId}/type")
    public ResponseEntity<List<DomesticStaffResponse>>
            getByType(
                @PathVariable Long communityId,
                @RequestParam StaffType type) {

        return ResponseEntity.ok(
            staffService.getByType(
                communityId, type));
    }

    // =====================================================
    // GET STAFF BY STATUS
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping("/community/{communityId}/status")
    public ResponseEntity<List<DomesticStaffResponse>>
            getByStatus(
                @PathVariable Long communityId,
                @RequestParam StaffStatus status) {

        return ResponseEntity.ok(
            staffService.getByStatus(
                communityId, status));
    }

    // =====================================================
    // GET STAFF BY VERIFICATION STATUS
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping(
        "/community/{communityId}/verification"
    )
    public ResponseEntity<List<DomesticStaffResponse>>
            getByVerification(
                @PathVariable Long communityId,
                @RequestParam VerificationStatus status) {

        return ResponseEntity.ok(
            staffService.getByVerificationStatus(
                communityId, status));
    }

    // =====================================================
    // VERIFY STAFF
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @PatchMapping("/{staffId}/verify")
    public ResponseEntity<DomesticStaffResponse> verify(
            @PathVariable Long staffId,
            @Valid @RequestBody
            VerifyStaffRequest request) {

        return ResponseEntity.ok(
            staffService.verifyStaff(
                staffId, request));
    }

    // =====================================================
    // BLOCK STAFF
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PatchMapping("/{staffId}/block")
    public ResponseEntity<DomesticStaffResponse> block(
            @PathVariable Long staffId,
            @Valid @RequestBody
            BlockStaffRequest request) {

        return ResponseEntity.ok(
            staffService.blockStaff(
                staffId, request));
    }

    // =====================================================
    // ACTIVATE STAFF
    // SUPER ADMIN + ADMIN + RESIDENT (own)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @PatchMapping("/{staffId}/activate")
    public ResponseEntity<DomesticStaffResponse> activate(
            @PathVariable Long staffId) {

        return ResponseEntity.ok(
            staffService.activateStaff(staffId));
    }

    // =====================================================
    // ASSIGN STAFF TO RESIDENT/FLAT
    // SUPER ADMIN + ADMIN + RESIDENT
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @PostMapping("/{staffId}/assign")
    public ResponseEntity<StaffAssignmentResponse> assign(
            @PathVariable Long staffId,
            @Valid @RequestBody
            StaffAssignmentRequest request) {

        return new ResponseEntity<>(
            staffService.assignToResident(
                staffId, request),
            HttpStatus.CREATED);
    }

    // =====================================================
    // GET STAFF ASSIGNMENTS
    // SUPER ADMIN + ADMIN + RESIDENT + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY')"
    )
    @GetMapping("/{staffId}/assignments")
    public ResponseEntity<List<StaffAssignmentResponse>>
            assignments(
                @PathVariable Long staffId) {

        return ResponseEntity.ok(
            staffService
                .getStaffAssignments(staffId));
    }

    // =====================================================
    // REMOVE ASSIGNMENT
    // SUPER ADMIN + ADMIN + RESIDENT
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @PatchMapping("/assignments/{assignmentId}/remove")
    public ResponseEntity<Void> removeAssignment(
            @PathVariable Long assignmentId) {

        staffService.removeAssignment(assignmentId);

        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // DELETE STAFF FROM SYSTEM
    // SUPER ADMIN + ADMIN + RESIDENT (who registered them)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @DeleteMapping("/{staffId}")
    public ResponseEntity<Void> deleteStaff(
            @PathVariable Long staffId) {

        staffService.deleteStaff(staffId);

        return ResponseEntity.noContent().build();
    }
}