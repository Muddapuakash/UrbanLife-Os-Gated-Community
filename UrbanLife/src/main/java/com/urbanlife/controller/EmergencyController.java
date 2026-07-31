package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.urbanlife.dto.CreateEmergencyRequest;
import com.urbanlife.dto.EmergencyResponse;
import com.urbanlife.dto.ResolveEmergencyRequest;
import com.urbanlife.enums.EmergencyPriority;
import com.urbanlife.enums.EmergencyStatus;
import com.urbanlife.enums.EmergencyType;
import com.urbanlife.service.EmergencyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/emergencies")
public class EmergencyController {

    private final EmergencyService emergencyService;

    public EmergencyController(
            EmergencyService emergencyService) {

        this.emergencyService = emergencyService;
    }

    // =====================================================
    // RAISE EMERGENCY
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @PostMapping
    public ResponseEntity<EmergencyResponse> create(
            @Valid @RequestBody
            CreateEmergencyRequest request) {

        return new ResponseEntity<>(
                emergencyService
                    .createEmergency(request),
                HttpStatus.CREATED);
    }

    // =====================================================
    // GET ALL EMERGENCIES
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping
    public ResponseEntity<List<EmergencyResponse>>
            getAll() {

        return ResponseEntity.ok(
                emergencyService
                    .getAllEmergencies());
    }

    // =====================================================
    // GET EMERGENCY BY ID
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/{emergencyId}")
    public ResponseEntity<EmergencyResponse> getById(
            @PathVariable Long emergencyId) {

        return ResponseEntity.ok(
                emergencyService
                    .getEmergencyById(emergencyId));
    }

    // =====================================================
    // GET EMERGENCIES BY RESIDENT
    // SUPER ADMIN + ADMIN + RESIDENT (own)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @GetMapping("/resident/{residentId}")
    public ResponseEntity<List<EmergencyResponse>>
            getByResident(
                    @PathVariable Long residentId) {

        return ResponseEntity.ok(
                emergencyService
                    .getByResident(residentId));
    }

    // =====================================================
    // GET COMMUNITY EMERGENCIES
    // SUPER ADMIN + ADMIN + SECURITY + STAFF
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<EmergencyResponse>>
            getByCommunity(
                    @PathVariable Long communityId) {

        return ResponseEntity.ok(
                emergencyService
                    .getByCommunity(communityId));
    }

    // =====================================================
    // GET BY STATUS
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping("/community/{communityId}/status")
    public ResponseEntity<List<EmergencyResponse>>
            getByStatus(
                    @PathVariable Long communityId,
                    @RequestParam EmergencyStatus status) {

        return ResponseEntity.ok(
                emergencyService
                    .getByStatus(
                        communityId,
                        status));
    }

    // =====================================================
    // GET BY PRIORITY
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping("/community/{communityId}/priority")
    public ResponseEntity<List<EmergencyResponse>>
            getByPriority(
                    @PathVariable Long communityId,
                    @RequestParam EmergencyPriority priority) {

        return ResponseEntity.ok(
                emergencyService
                    .getByPriority(
                        communityId,
                        priority));
    }

    // =====================================================
    // GET BY TYPE
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping("/community/{communityId}/type")
    public ResponseEntity<List<EmergencyResponse>>
            getByType(
                    @PathVariable Long communityId,
                    @RequestParam EmergencyType type) {

        return ResponseEntity.ok(
                emergencyService
                    .getByType(
                        communityId,
                        type));
    }

    // =====================================================
    // ACKNOWLEDGE EMERGENCY
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @PatchMapping("/{emergencyId}/acknowledge")
    public ResponseEntity<EmergencyResponse>
            acknowledge(
                    @PathVariable Long emergencyId) {

        return ResponseEntity.ok(
                emergencyService
                    .acknowledgeEmergency(
                        emergencyId));
    }

    // =====================================================
    // ASSIGN RESPONDER
    // SUPER ADMIN + ADMIN ONLY
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PatchMapping("/{emergencyId}/assign/{userId}")
    public ResponseEntity<EmergencyResponse>
            assignResponder(
                    @PathVariable Long emergencyId,
                    @PathVariable Long userId) {

        return ResponseEntity.ok(
                emergencyService
                    .assignResponder(
                        emergencyId,
                        userId));
    }

    // =====================================================
    // START RESPONSE
    // SUPER ADMIN + ADMIN + SECURITY + STAFF
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY', 'STAFF')"
    )
    @PatchMapping("/{emergencyId}/start")
    public ResponseEntity<EmergencyResponse>
            startResponse(
                    @PathVariable Long emergencyId) {

        return ResponseEntity.ok(
                emergencyService
                    .startResponse(emergencyId));
    }

    // =====================================================
    // RESOLVE EMERGENCY
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @PatchMapping("/{emergencyId}/resolve")
    public ResponseEntity<EmergencyResponse> resolve(
            @PathVariable Long emergencyId,
            @Valid @RequestBody
            ResolveEmergencyRequest request) {

        return ResponseEntity.ok(
                emergencyService
                    .resolveEmergency(
                        emergencyId,
                        request));
    }

    // =====================================================
    // CANCEL EMERGENCY
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @PatchMapping("/{emergencyId}/cancel")
    public ResponseEntity<EmergencyResponse> cancel(
            @PathVariable Long emergencyId) {

        return ResponseEntity.ok(
                emergencyService
                    .cancelEmergency(emergencyId));
    }
}