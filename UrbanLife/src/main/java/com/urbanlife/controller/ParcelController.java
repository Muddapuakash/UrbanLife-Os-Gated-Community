package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.urbanlife.dto.CollectParcelRequest;
import com.urbanlife.dto.CreateParcelRequest;
import com.urbanlife.dto.ParcelResponse;
import com.urbanlife.dto.ReturnParcelRequest;
import com.urbanlife.enums.ParcelStatus;
import com.urbanlife.enums.ParcelType;
import com.urbanlife.service.ParcelService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/parcels")
public class ParcelController {

    private final ParcelService parcelService;

    public ParcelController(
            ParcelService parcelService) {

        this.parcelService = parcelService;
    }

    // =====================================================
    // CREATE PARCEL ENTRY (log delivery)
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @PostMapping
    public ResponseEntity<ParcelResponse> create(
            @Valid @RequestBody
            CreateParcelRequest request) {

        return new ResponseEntity<>(
                parcelService.createParcel(request),
                HttpStatus.CREATED);
    }

    // =====================================================
    // GET ALL PARCELS
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping
    public ResponseEntity<List<ParcelResponse>>
            getAll() {

        return ResponseEntity.ok(
                parcelService.getAllParcels());
    }

    // =====================================================
    // GET PARCEL BY ID
    // SUPER ADMIN + ADMIN + RESIDENT (own) + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY')"
    )
    @GetMapping("/{parcelId}")
    public ResponseEntity<ParcelResponse> getById(
            @PathVariable Long parcelId) {

        return ResponseEntity.ok(
                parcelService
                    .getParcelById(parcelId));
    }

    // =====================================================
    // GET PARCELS BY RESIDENT
    // SUPER ADMIN + ADMIN + RESIDENT (own) + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY')"
    )
    @GetMapping("/resident/{residentId}")
    public ResponseEntity<List<ParcelResponse>>
            getByResident(
                    @PathVariable Long residentId) {

        return ResponseEntity.ok(
                parcelService
                    .getByResident(residentId));
    }

    // =====================================================
    // GET PARCELS BY FLAT
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY')"
    )
    @GetMapping("/flat/{flatId}")
    public ResponseEntity<List<ParcelResponse>>
            getByFlat(
                    @PathVariable Long flatId) {

        return ResponseEntity.ok(
                parcelService
                    .getByFlat(flatId));
    }

    // =====================================================
    // GET PARCELS BY COMMUNITY
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<ParcelResponse>>
            getByCommunity(
                    @PathVariable Long communityId) {

        return ResponseEntity.ok(
                parcelService
                    .getByCommunity(communityId));
    }

    // =====================================================
    // GET PARCELS BY STATUS
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping("/community/{communityId}/status")
    public ResponseEntity<List<ParcelResponse>>
            getByStatus(
                    @PathVariable Long communityId,
                    @RequestParam ParcelStatus status) {

        return ResponseEntity.ok(
                parcelService
                    .getByStatus(
                        communityId,
                        status));
    }

    // =====================================================
    // GET PARCELS BY TYPE
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping("/community/{communityId}/type")
    public ResponseEntity<List<ParcelResponse>>
            getByType(
                    @PathVariable Long communityId,
                    @RequestParam ParcelType type) {

        return ResponseEntity.ok(
                parcelService
                    .getByType(
                        communityId,
                        type));
    }

    // =====================================================
    // NOTIFY RESIDENT (mark as notified)
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @PatchMapping("/{parcelId}/notify")
    public ResponseEntity<ParcelResponse> notifyResident(
            @PathVariable Long parcelId) {

        return ResponseEntity.ok(
                parcelService
                    .markAsNotified(parcelId));
    }

    // =====================================================
    // COLLECT PARCEL
    // SUPER ADMIN + ADMIN + RESIDENT (own) + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY')"
    )
    @PatchMapping("/{parcelId}/collect")
    public ResponseEntity<ParcelResponse> collect(
            @PathVariable Long parcelId,
            @Valid @RequestBody
            CollectParcelRequest request) {

        return ResponseEntity.ok(
                parcelService
                    .collectParcel(
                        parcelId,
                        request));
    }

    // =====================================================
    // RETURN PARCEL
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @PatchMapping("/{parcelId}/return")
    public ResponseEntity<ParcelResponse> returnParcel(
            @PathVariable Long parcelId,
            @Valid @RequestBody
            ReturnParcelRequest request) {

        return ResponseEntity.ok(
                parcelService
                    .returnParcel(
                        parcelId,
                        request));
    }

    // =====================================================
    // GET PENDING PARCEL COUNT
    // SUPER ADMIN + ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping(
        "/community/{communityId}/pending/count"
    )
    public ResponseEntity<Long> getPendingCount(
            @PathVariable Long communityId) {

        return ResponseEntity.ok(
                parcelService
                    .getPendingParcelCount(
                        communityId));
    }
}