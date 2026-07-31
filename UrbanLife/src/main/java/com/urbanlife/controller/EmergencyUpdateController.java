package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.urbanlife.dto.EmergencyUpdateRequest;
import com.urbanlife.dto.EmergencyUpdateResponse;
import com.urbanlife.service.EmergencyUpdateService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/emergency-updates")
public class EmergencyUpdateController {

    private final EmergencyUpdateService updateService;

    public EmergencyUpdateController(
            EmergencyUpdateService updateService) {

        this.updateService = updateService;
    }

    // =====================================================
    // ADD EMERGENCY UPDATE
    // SUPER ADMIN + ADMIN + SECURITY + STAFF
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY', 'STAFF')"
    )
    @PostMapping("/emergency/{emergencyId}")
    public ResponseEntity<EmergencyUpdateResponse>
            addUpdate(
                    @PathVariable Long emergencyId,
                    @Valid @RequestBody
                    EmergencyUpdateRequest request) {

        return new ResponseEntity<>(
                updateService.addUpdate(
                    emergencyId,
                    request),
                HttpStatus.CREATED);
    }

    // =====================================================
    // GET EMERGENCY UPDATE TIMELINE
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/emergency/{emergencyId}")
    public ResponseEntity<
            List<EmergencyUpdateResponse>>
            getTimeline(
                    @PathVariable Long emergencyId) {

        return ResponseEntity.ok(
                updateService
                    .getTimeline(emergencyId));
    }
}