package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.urbanlife.dto.*;
import com.urbanlife.service.StaffRatingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/staff-ratings")
public class StaffRatingController {

    private final StaffRatingService ratingService;

    public StaffRatingController(
            StaffRatingService ratingService) {

        this.ratingService = ratingService;
    }

    // =====================================================
    // ADD RATING
    // RESIDENT ONLY (only residents rate staff per matrix)
    // =====================================================

    @PreAuthorize("hasRole('RESIDENT')")
    @PostMapping("/{staffId}")
    public ResponseEntity<StaffRatingResponse> addRating(
            @PathVariable Long staffId,
            @Valid @RequestBody
            StaffRatingRequest request) {

        return new ResponseEntity<>(
            ratingService.addRating(
                staffId, request),
            HttpStatus.CREATED);
    }

    // =====================================================
    // GET STAFF RATINGS
    // SUPER ADMIN + ADMIN + RESIDENT + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY')"
    )
    @GetMapping("/{staffId}")
    public ResponseEntity<List<StaffRatingResponse>>
            getRatings(
                @PathVariable Long staffId) {

        return ResponseEntity.ok(
            ratingService
                .getStaffRatings(staffId));
    }

    // =====================================================
    // GET AVERAGE RATING
    // SUPER ADMIN + ADMIN + RESIDENT + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY')"
    )
    @GetMapping("/{staffId}/average")
    public ResponseEntity<Double> average(
            @PathVariable Long staffId) {

        return ResponseEntity.ok(
            ratingService
                .getAverageRating(staffId));
    }
}