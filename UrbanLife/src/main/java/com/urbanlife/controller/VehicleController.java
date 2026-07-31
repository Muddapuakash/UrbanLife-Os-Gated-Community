package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.urbanlife.dto.CreateVehicleRequest;
import com.urbanlife.dto.VehicleResponse;
import com.urbanlife.service.VehicleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(
            VehicleService vehicleService) {

        this.vehicleService = vehicleService;
    }

    // =====================================================
    // CREATE / REGISTER VEHICLE
    // SUPER ADMIN + ADMIN + RESIDENT
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle(
            @Valid @RequestBody CreateVehicleRequest request) {

        return new ResponseEntity<>(
                vehicleService.createVehicle(request),
                HttpStatus.CREATED);
    }

    // =====================================================
    // GET ALL VEHICLES
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping
    public ResponseEntity<List<VehicleResponse>>
            getAllVehicles() {

        return ResponseEntity.ok(
                vehicleService.getAllVehicles());
    }

    // =====================================================
    // GET VEHICLE BY ID
    // SUPER ADMIN + ADMIN + RESIDENT + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY')"
    )
    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleResponse> getById(
            @PathVariable Long vehicleId) {

        return ResponseEntity.ok(
                vehicleService.getVehicleById(vehicleId));
    }

    // =====================================================
    // GET VEHICLE BY VEHICLE NUMBER
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping("/number/{vehicleNumber}")
    public ResponseEntity<VehicleResponse> getByNumber(
            @PathVariable String vehicleNumber) {

        return ResponseEntity.ok(
                vehicleService
                    .getVehicleByNumber(vehicleNumber));
    }

    // =====================================================
    // GET VEHICLES BY RESIDENT
    // SUPER ADMIN + ADMIN + RESIDENT
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @GetMapping("/resident/{residentId}")
    public ResponseEntity<List<VehicleResponse>>
            getByResident(
                    @PathVariable Long residentId) {

        return ResponseEntity.ok(
                vehicleService
                    .getVehiclesByResident(residentId));
    }

    // =====================================================
    // GET VEHICLES BY COMMUNITY
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<VehicleResponse>>
            getByCommunity(
                    @PathVariable Long communityId) {

        return ResponseEntity.ok(
                vehicleService
                    .getVehiclesByCommunity(communityId));
    }

    // =====================================================
    // DEACTIVATE VEHICLE
    // SUPER ADMIN + ADMIN + RESIDENT
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @PatchMapping("/{vehicleId}/deactivate")
    public ResponseEntity<VehicleResponse> deactivate(
            @PathVariable Long vehicleId) {

        return ResponseEntity.ok(
                vehicleService
                    .deactivateVehicle(vehicleId));
    }
}