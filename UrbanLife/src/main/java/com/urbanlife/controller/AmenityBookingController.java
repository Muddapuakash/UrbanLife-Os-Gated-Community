package com.urbanlife.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.urbanlife.dto.AmenityBookingResponse;
import com.urbanlife.dto.CancelBookingRequest;
import com.urbanlife.dto.CreateAmenityBookingRequest;
import com.urbanlife.service.AmenityBookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/amenity-bookings")
public class AmenityBookingController {

    private final AmenityBookingService bookingService;

    public AmenityBookingController(
            AmenityBookingService bookingService) {

        this.bookingService = bookingService;
    }

    // =====================================================
    // CREATE BOOKING
    // SUPER ADMIN + ADMIN + RESIDENT
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @PostMapping
    public ResponseEntity<AmenityBookingResponse> create(
            @Valid @RequestBody
            CreateAmenityBookingRequest request) {

        return new ResponseEntity<>(
                bookingService.createBooking(request),
                HttpStatus.CREATED);
    }

    // =====================================================
    // GET ALL BOOKINGS
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<AmenityBookingResponse>>
            getAll() {

        return ResponseEntity.ok(
                bookingService.getAllBookings());
    }

    // =====================================================
    // GET BOOKING BY ID
    // SUPER ADMIN + ADMIN + RESIDENT
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @GetMapping("/{bookingId}")
    public ResponseEntity<AmenityBookingResponse> getById(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                bookingService.getBookingById(bookingId));
    }

    // =====================================================
    // GET BOOKINGS BY RESIDENT
    // SUPER ADMIN + ADMIN + RESIDENT
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @GetMapping("/resident/{residentId}")
    public ResponseEntity<List<AmenityBookingResponse>>
            getByResident(@PathVariable Long residentId) {

        return ResponseEntity.ok(
                bookingService.getByResident(residentId));
    }

    // =====================================================
    // GET BOOKINGS BY AMENITY
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/amenity/{amenityId}")
    public ResponseEntity<List<AmenityBookingResponse>>
            getByAmenity(@PathVariable Long amenityId) {

        return ResponseEntity.ok(
                bookingService.getByAmenity(amenityId));
    }

    // =====================================================
    // GET BOOKINGS BY AMENITY AND DATE
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/amenity/{amenityId}/date")
    public ResponseEntity<List<AmenityBookingResponse>>
            getByAmenityAndDate(
                    @PathVariable Long amenityId,
                    @RequestParam LocalDate date) {

        return ResponseEntity.ok(
                bookingService.getByAmenityAndDate(
                    amenityId,
                    date));
    }

    // =====================================================
    // GET BOOKINGS BY COMMUNITY
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<AmenityBookingResponse>>
            getByCommunity(@PathVariable Long communityId) {

        return ResponseEntity.ok(
                bookingService.getByCommunity(communityId));
    }

    // =====================================================
    // CANCEL BOOKING
    // SUPER ADMIN + ADMIN + RESIDENT
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<AmenityBookingResponse> cancel(
            @PathVariable Long bookingId,
            @Valid @RequestBody CancelBookingRequest request) {

        return ResponseEntity.ok(
                bookingService.cancelBooking(
                    bookingId,
                    request));
    }

    // =====================================================
    // COMPLETE BOOKING (mark as done)
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PatchMapping("/{bookingId}/complete")
    public ResponseEntity<AmenityBookingResponse> complete(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                bookingService.completeBooking(bookingId));
    }
}