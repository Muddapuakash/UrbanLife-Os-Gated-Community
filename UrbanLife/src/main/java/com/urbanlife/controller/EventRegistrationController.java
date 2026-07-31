package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.urbanlife.dto.EventRegistrationResponse;
import com.urbanlife.service.EventRegistrationService;

@RestController
@RequestMapping("/api/v1/event-registrations")
public class EventRegistrationController {

    private final EventRegistrationService registrationService;

    public EventRegistrationController(
            EventRegistrationService registrationService) {

        this.registrationService =
                registrationService;
    }

    // =====================================================
    // REGISTER FOR EVENT
    // SUPER ADMIN + ADMIN + RESIDENT + SECURITY + STAFF
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @PostMapping("/event/{eventId}/user/{userId}")
    public ResponseEntity<EventRegistrationResponse>
            register(
                @PathVariable Long eventId,
                @PathVariable Long userId) {

        return new ResponseEntity<>(
                registrationService.register(
                    eventId,
                    userId),
                HttpStatus.CREATED);
    }

    // =====================================================
    // CANCEL REGISTRATION
    // ALL AUTHENTICATED ROLES (own registration)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @PatchMapping(
        "/event/{eventId}/user/{userId}/cancel"
    )
    public ResponseEntity<EventRegistrationResponse>
            cancel(
                @PathVariable Long eventId,
                @PathVariable Long userId) {

        return ResponseEntity.ok(
                registrationService
                    .cancelRegistration(
                        eventId,
                        userId));
    }

    // =====================================================
    // MARK ATTENDANCE
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PatchMapping(
        "/event/{eventId}/user/{userId}/attendance"
    )
    public ResponseEntity<EventRegistrationResponse>
            attendance(
                @PathVariable Long eventId,
                @PathVariable Long userId) {

        return ResponseEntity.ok(
                registrationService
                    .markAttendance(
                        eventId,
                        userId));
    }

    // =====================================================
    // GET EVENT REGISTRATIONS
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/event/{eventId}")
    public ResponseEntity<
            List<EventRegistrationResponse>>
            eventRegistrations(
                @PathVariable Long eventId) {

        return ResponseEntity.ok(
                registrationService
                    .getEventRegistrations(
                        eventId));
    }

    // =====================================================
    // GET USER REGISTRATIONS
    // ALL AUTHENTICATED ROLES (own history)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<
            List<EventRegistrationResponse>>
            userRegistrations(
                @PathVariable Long userId) {

        return ResponseEntity.ok(
                registrationService
                    .getUserRegistrations(
                        userId));
    }
}