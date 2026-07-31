package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.urbanlife.dto.PollRequest;
import com.urbanlife.dto.PollResponse;
import com.urbanlife.enums.PollStatus;
import com.urbanlife.service.PollService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/polls")
public class PollController {

    private final PollService pollService;

    public PollController(
            PollService pollService) {

        this.pollService = pollService;
    }

    // =====================================================
    // CREATE POLL
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public ResponseEntity<PollResponse>
            create(
                @Valid @RequestBody
                PollRequest request) {

        return new ResponseEntity<>(
                pollService.createPoll(request),
                HttpStatus.CREATED);
    }

    // =====================================================
    // GET POLL BY ID
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/{pollId}")
    public ResponseEntity<PollResponse>
            getPoll(
                @PathVariable Long pollId) {

        return ResponseEntity.ok(
                pollService.getPoll(pollId));
    }

    // =====================================================
    // GET COMMUNITY POLLS
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping(
        "/community/{communityId}"
    )
    public ResponseEntity<List<PollResponse>>
            communityPolls(
                @PathVariable Long communityId) {

        return ResponseEntity.ok(
                pollService
                    .getCommunityPolls(
                        communityId));
    }

    // =====================================================
    // GET POLLS BY STATUS
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping(
        "/community/{communityId}/status"
    )
    public ResponseEntity<List<PollResponse>>
            pollsByStatus(
                @PathVariable Long communityId,
                @RequestParam PollStatus status) {

        return ResponseEntity.ok(
                pollService
                    .getPollsByStatus(
                        communityId,
                        status));
    }

    // =====================================================
    // GET POLLS CREATED BY USER
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/created-by/{userId}")
    public ResponseEntity<List<PollResponse>>
            createdPolls(
                @PathVariable Long userId) {

        return ResponseEntity.ok(
                pollService
                    .getCreatedPolls(userId));
    }

    // =====================================================
    // UPDATE POLL
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PutMapping("/{pollId}")
    public ResponseEntity<PollResponse>
            update(
                @PathVariable Long pollId,
                @Valid @RequestBody
                PollRequest request) {

        return ResponseEntity.ok(
                pollService.updatePoll(
                    pollId,
                    request));
    }

    // =====================================================
    // ACTIVATE POLL
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PatchMapping("/{pollId}/activate")
    public ResponseEntity<PollResponse>
            activate(
                @PathVariable Long pollId) {

        return ResponseEntity.ok(
                pollService
                    .activatePoll(pollId));
    }

    // =====================================================
    // CLOSE POLL
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PatchMapping("/{pollId}/close")
    public ResponseEntity<PollResponse>
            close(
                @PathVariable Long pollId) {

        return ResponseEntity.ok(
                pollService
                    .closePoll(pollId));
    }

    // =====================================================
    // CANCEL POLL
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PatchMapping("/{pollId}/cancel")
    public ResponseEntity<PollResponse>
            cancel(
                @PathVariable Long pollId) {

        return ResponseEntity.ok(
                pollService
                    .cancelPoll(pollId));
    }

    // =====================================================
    // DELETE POLL
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @DeleteMapping("/{pollId}")
    public ResponseEntity<Void>
            delete(
                @PathVariable Long pollId) {

        pollService.deletePoll(pollId);

        return ResponseEntity
                .noContent()
                .build();
    }
}