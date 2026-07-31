package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.urbanlife.dto.CreateParkingAllocationRequest;
import com.urbanlife.dto.ParkingAllocationResponse;
import com.urbanlife.service.ParkingAllocationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/parking-allocations")
public class ParkingAllocationController {

    private final ParkingAllocationService parkingAllocationService;

    public ParkingAllocationController(
            ParkingAllocationService parkingAllocationService) {

        this.parkingAllocationService =
                parkingAllocationService;
    }

    // =====================================================
    // ALLOCATE PARKING
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ParkingAllocationResponse> allocate(
            @Valid @RequestBody
            CreateParkingAllocationRequest request) {

        return new ResponseEntity<>(
                parkingAllocationService
                    .allocateParking(request),
                HttpStatus.CREATED);
    }

    // =====================================================
    // GET ALLOCATION BY ID
    // SUPER ADMIN + ADMIN + RESIDENT + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY')"
    )
    @GetMapping("/{allocationId}")
    public ResponseEntity<ParkingAllocationResponse>
            getById(
                    @PathVariable Long allocationId) {

        return ResponseEntity.ok(
                parkingAllocationService
                    .getAllocationById(allocationId));
    }

    // =====================================================
    // GET ALL ACTIVE ALLOCATIONS
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping("/active")
    public ResponseEntity<List<ParkingAllocationResponse>>
            getActive() {

        return ResponseEntity.ok(
                parkingAllocationService
                    .getActiveAllocations());
    }

    // =====================================================
    // GET ALLOCATIONS BY RESIDENT
    // SUPER ADMIN + ADMIN + RESIDENT + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY')"
    )
    @GetMapping("/resident/{residentId}")
    public ResponseEntity<List<ParkingAllocationResponse>>
            getByResident(
                    @PathVariable Long residentId) {

        return ResponseEntity.ok(
                parkingAllocationService
                    .getByResident(residentId));
    }

    // =====================================================
    // GET ALLOCATIONS BY COMMUNITY
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<ParkingAllocationResponse>>
            getByCommunity(
                    @PathVariable Long communityId) {

        return ResponseEntity.ok(
                parkingAllocationService
                    .getByCommunity(communityId));
    }

    // =====================================================
    // RELEASE PARKING
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PatchMapping("/{allocationId}/release")
    public ResponseEntity<ParkingAllocationResponse> release(
            @PathVariable Long allocationId) {

        return ResponseEntity.ok(
                parkingAllocationService
                    .releaseParking(allocationId));
    }
}