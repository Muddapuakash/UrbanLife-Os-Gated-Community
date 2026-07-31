package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.urbanlife.dto.AmenityResponse;
import com.urbanlife.dto.CreateAmenityRequest;
import com.urbanlife.enums.AmenityStatus;
import com.urbanlife.enums.AmenityType;
import com.urbanlife.service.AmenityService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/amenities")
public class AmenityController {

    private final AmenityService amenityService;

    public AmenityController(AmenityService amenityService) {
        this.amenityService = amenityService;
    }

    // =====================================================
    // CREATE AMENITY
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public ResponseEntity<AmenityResponse> create(
            @Valid @RequestBody CreateAmenityRequest request) {

        return new ResponseEntity<>(
                amenityService.createAmenity(request),
                HttpStatus.CREATED);
    }

    // =====================================================
    // GET ALL AMENITIES
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping
    public ResponseEntity<List<AmenityResponse>> getAll() {

        return ResponseEntity.ok(
                amenityService.getAllAmenities());
    }

    // =====================================================
    // GET AMENITY BY ID
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/{amenityId}")
    public ResponseEntity<AmenityResponse> getById(
            @PathVariable Long amenityId) {

        return ResponseEntity.ok(
                amenityService.getAmenityById(amenityId));
    }

    // =====================================================
    // GET AMENITIES BY COMMUNITY
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<AmenityResponse>>
            getByCommunity(@PathVariable Long communityId) {

        return ResponseEntity.ok(
                amenityService.getByCommunity(communityId));
    }

    // =====================================================
    // GET AMENITIES BY STATUS
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/community/{communityId}/status")
    public ResponseEntity<List<AmenityResponse>> getByStatus(
            @PathVariable Long communityId,
            @RequestParam AmenityStatus status) {

        return ResponseEntity.ok(
                amenityService.getByStatus(
                    communityId,
                    status));
    }

    // =====================================================
    // GET AMENITIES BY TYPE
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/community/{communityId}/type")
    public ResponseEntity<List<AmenityResponse>> getByType(
            @PathVariable Long communityId,
            @RequestParam AmenityType type) {

        return ResponseEntity.ok(
                amenityService.getByType(
                    communityId,
                    type));
    }

    // =====================================================
    // UPDATE AMENITY STATUS
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PatchMapping("/{amenityId}/status")
    public ResponseEntity<AmenityResponse> updateStatus(
            @PathVariable Long amenityId,
            @RequestParam AmenityStatus status) {

        return ResponseEntity.ok(
                amenityService.updateStatus(
                    amenityId,
                    status));
    }
}