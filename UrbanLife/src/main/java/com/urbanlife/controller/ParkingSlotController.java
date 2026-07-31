package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.urbanlife.dto.CreateParkingSlotRequest;
import com.urbanlife.dto.ParkingSlotResponse;
import com.urbanlife.enums.ParkingSlotStatus;
import com.urbanlife.service.ParkingSlotService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/parking-slots")
public class ParkingSlotController {

    private final ParkingSlotService parkingSlotService;

    public ParkingSlotController(
            ParkingSlotService parkingSlotService) {

        this.parkingSlotService = parkingSlotService;
    }

    // =====================================================
    // CREATE PARKING SLOT
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ParkingSlotResponse> create(
            @Valid @RequestBody CreateParkingSlotRequest request) {

        return new ResponseEntity<>(
                parkingSlotService.createParkingSlot(request),
                HttpStatus.CREATED);
    }

    // =====================================================
    // GET ALL SLOTS
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping
    public ResponseEntity<List<ParkingSlotResponse>> getAll() {

        return ResponseEntity.ok(
                parkingSlotService.getAllParkingSlots());
    }

    // =====================================================
    // GET SLOT BY ID
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ParkingSlotResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                parkingSlotService.getParkingSlotById(id));
    }

    // =====================================================
    // GET SLOTS BY COMMUNITY
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<ParkingSlotResponse>>
            getByCommunity(
                    @PathVariable Long communityId) {

        return ResponseEntity.ok(
                parkingSlotService
                    .getSlotsByCommunity(communityId));
    }

    // =====================================================
    // GET SLOTS BY STATUS (includes available search)
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/community/{communityId}/status")
    public ResponseEntity<List<ParkingSlotResponse>>
            getByStatus(
                    @PathVariable Long communityId,
                    @RequestParam ParkingSlotStatus status) {

        return ResponseEntity.ok(
                parkingSlotService
                    .getSlotsByStatus(communityId, status));
    }
}