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

import com.urbanlife.dto.CreateFlatRequest;
import com.urbanlife.dto.FlatResponse;
import com.urbanlife.dto.UpdateFlatRequest;
import com.urbanlife.enums.FlatStatus;
import com.urbanlife.service.FlatService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/flats")
public class FlatController {

    private final FlatService flatService;

    public FlatController(FlatService flatService) {
        this.flatService = flatService;
    }

    // =====================================================
    // CREATE FLAT
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public ResponseEntity<FlatResponse> createFlat(
            @Valid @RequestBody CreateFlatRequest request) {

        return new ResponseEntity<>(
                flatService.createFlat(request),
                HttpStatus.CREATED
        );
    }

    // =====================================================
    // GET FLAT BY ID
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/{flatId}")
    public ResponseEntity<FlatResponse> getFlatById(
            @PathVariable Long flatId) {

        return ResponseEntity.ok(
                flatService.getFlatById(flatId)
        );
    }

    // =====================================================
    // GET ALL FLATS
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<FlatResponse>> getAllFlats() {

        return ResponseEntity.ok(
                flatService.getAllFlats()
        );
    }

    // =====================================================
    // GET FLATS BY BLOCK
    // SUPER ADMIN + ADMIN + SECURITY + STAFF
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/block/{blockId}")
    public ResponseEntity<List<FlatResponse>> getFlatsByBlock(
            @PathVariable Long blockId) {

        return ResponseEntity.ok(
                flatService.getFlatsByBlock(blockId)
        );
    }

    // =====================================================
    // GET FLATS BY COMMUNITY
    // SUPER ADMIN + ADMIN + SECURITY + STAFF
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<FlatResponse>>
            getFlatsByCommunity(
                    @PathVariable Long communityId) {

        return ResponseEntity.ok(
                flatService.getFlatsByCommunity(communityId)
        );
    }

    // =====================================================
    // SEARCH FLATS BY STATUS
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/search/status")
    public ResponseEntity<List<FlatResponse>> getFlatsByStatus(
            @RequestParam FlatStatus status) {

        return ResponseEntity.ok(
                flatService.getFlatsByStatus(status)
        );
    }

    // =====================================================
    // UPDATE FLAT
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PutMapping("/{flatId}")
    public ResponseEntity<FlatResponse> updateFlat(
            @PathVariable Long flatId,
            @Valid @RequestBody UpdateFlatRequest request) {

        return ResponseEntity.ok(
                flatService.updateFlat(flatId, request)
        );
    }

    // =====================================================
    // DELETE FLAT
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @DeleteMapping("/{flatId}")
    public ResponseEntity<Void> deleteFlat(
            @PathVariable Long flatId) {

        flatService.deleteFlat(flatId);

        return ResponseEntity.noContent().build();
    }
}