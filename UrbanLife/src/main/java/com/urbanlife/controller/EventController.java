package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.urbanlife.dto.EventRequest;
import com.urbanlife.dto.EventResponse;
import com.urbanlife.enums.EventStatus;
import com.urbanlife.service.EventService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(
            EventService eventService) {
        this.eventService = eventService;
    }

    // =====================================================
    // CREATE EVENT
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public ResponseEntity<EventResponse> create(
            @Valid @RequestBody EventRequest request) {

        return new ResponseEntity<>(
                eventService.createEvent(request),
                HttpStatus.CREATED);
    }

    // =====================================================
    // GET EVENT BY ID
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getById(
            @PathVariable Long eventId) {

        return ResponseEntity.ok(
                eventService.getEventById(eventId));
    }

    // =====================================================
    // GET COMMUNITY EVENTS
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<EventResponse>>
            getCommunityEvents(
                @PathVariable Long communityId) {

        return ResponseEntity.ok(
                eventService
                    .getCommunityEvents(communityId));
    }

    // =====================================================
    // GET EVENTS BY STATUS
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/community/{communityId}/status")
    public ResponseEntity<List<EventResponse>>
            getByStatus(
                @PathVariable Long communityId,
                @RequestParam EventStatus status) {

        return ResponseEntity.ok(
                eventService.getEventsByStatus(
                    communityId,
                    status));
    }

    // =====================================================
    // UPDATE EVENT
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponse> update(
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequest request) {

        return ResponseEntity.ok(
                eventService.updateEvent(
                    eventId,
                    request));
    }

    // =====================================================
    // PUBLISH EVENT
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PatchMapping("/{eventId}/publish")
    public ResponseEntity<EventResponse> publish(
            @PathVariable Long eventId) {

        return ResponseEntity.ok(
                eventService.publishEvent(eventId));
    }

    // =====================================================
    // CANCEL EVENT
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PatchMapping("/{eventId}/cancel")
    public ResponseEntity<EventResponse> cancel(
            @PathVariable Long eventId) {

        return ResponseEntity.ok(
                eventService.cancelEvent(eventId));
    }

    // =====================================================
    // COMPLETE EVENT
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PatchMapping("/{eventId}/complete")
    public ResponseEntity<EventResponse> complete(
            @PathVariable Long eventId) {

        return ResponseEntity.ok(
                eventService.completeEvent(eventId));
    }

    // =====================================================
    // DELETE EVENT
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long eventId) {

        eventService.deleteEvent(eventId);

        return ResponseEntity.noContent().build();
    }
}