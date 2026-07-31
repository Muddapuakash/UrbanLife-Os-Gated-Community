package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.urbanlife.dto.CreateResidentRequest;
import com.urbanlife.dto.ResidentResponse;
import com.urbanlife.dto.UpdateResidentRequest;
import com.urbanlife.enums.ResidentStatus;
import com.urbanlife.service.ResidentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/residents")
public class ResidentController {

    private final ResidentService residentService;

    public ResidentController(
            ResidentService residentService) {

        this.residentService = residentService;
    }

    // =====================================================
    // CREATE RESIDENT
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN')"
    )
    @PostMapping
    public ResponseEntity<ResidentResponse> createResident(
            @Valid @RequestBody CreateResidentRequest request) {

        return new ResponseEntity<>(
                residentService.createResident(request),
                HttpStatus.CREATED
        );
    }

    // =====================================================
    // GET RESIDENT BY ID
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/{residentId}")
    public ResponseEntity<ResidentResponse> getResidentById(
            @PathVariable Long residentId) {

        return ResponseEntity.ok(
                residentService.getResidentById(residentId)
        );
    }

    // =====================================================
    // GET ALL RESIDENTS
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN')"
    )
    @GetMapping
    public ResponseEntity<List<ResidentResponse>>
            getAllResidents() {

        return ResponseEntity.ok(
                residentService.getAllResidents()
        );
    }

    // =====================================================
    // GET RESIDENT BY USER ID
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResidentResponse>
            getResidentByUserId(
                    @PathVariable Long userId) {

        return ResponseEntity.ok(
                residentService.getResidentByUserId(userId)
        );
    }

    // =====================================================
    // GET RESIDENTS BY FLAT
    // SUPER ADMIN + ADMIN + SECURITY + STAFF
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/flat/{flatId}")
    public ResponseEntity<List<ResidentResponse>>
            getResidentsByFlat(
                    @PathVariable Long flatId) {

        return ResponseEntity.ok(
                residentService.getResidentsByFlat(flatId)
        );
    }

    // =====================================================
    // GET RESIDENTS BY BLOCK
    // SUPER ADMIN + ADMIN + SECURITY + STAFF
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/block/{blockId}")
    public ResponseEntity<List<ResidentResponse>>
            getResidentsByBlock(
                    @PathVariable Long blockId) {

        return ResponseEntity.ok(
                residentService.getResidentsByBlock(blockId)
        );
    }

    // =====================================================
    // GET RESIDENTS BY COMMUNITY
    // SUPER ADMIN + ADMIN + SECURITY + STAFF
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<ResidentResponse>>
            getResidentsByCommunity(
                    @PathVariable Long communityId) {

        return ResponseEntity.ok(
                residentService
                    .getResidentsByCommunity(communityId)
        );
    }

    // =====================================================
    // SEARCH RESIDENTS BY STATUS
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN')"
    )
    @GetMapping("/search/status")
    public ResponseEntity<List<ResidentResponse>>
            getResidentsByStatus(
                    @RequestParam ResidentStatus status) {

        return ResponseEntity.ok(
                residentService.getResidentsByStatus(status)
        );
    }

    // =====================================================
    // UPDATE RESIDENT
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN')"
    )
    @PutMapping("/{residentId}")
    public ResponseEntity<ResidentResponse> updateResident(
            @PathVariable Long residentId,
            @Valid @RequestBody UpdateResidentRequest request) {

        return ResponseEntity.ok(
                residentService.updateResident(
                        residentId,
                        request)
        );
    }

    // =====================================================
    // DELETE RESIDENT
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN')"
    )
    @DeleteMapping("/{residentId}")
    public ResponseEntity<Void> deleteResident(
            @PathVariable Long residentId) {

        residentService.deleteResident(residentId);

        return ResponseEntity.noContent().build();
    }
}