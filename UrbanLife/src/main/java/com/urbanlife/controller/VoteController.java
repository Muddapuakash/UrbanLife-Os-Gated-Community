package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.urbanlife.dto.VoteRequest;
import com.urbanlife.dto.VoteResponse;
import com.urbanlife.service.VoteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/votes")
public class VoteController {

    private final VoteService voteService;

    public VoteController(
            VoteService voteService) {

        this.voteService = voteService;
    }

    // =====================================================
    // CAST VOTE
    // RESIDENT ONLY (only residents vote per matrix)
    // =====================================================

    @PreAuthorize("hasRole('RESIDENT')")
    @PostMapping(
        "/poll/{pollId}/user/{userId}"
    )
    public ResponseEntity<VoteResponse>
            vote(
                @PathVariable Long pollId,
                @PathVariable Long userId,
                @Valid @RequestBody
                VoteRequest request) {

        return new ResponseEntity<>(
                voteService.castVote(
                    pollId,
                    userId,
                    request),
                HttpStatus.CREATED);
    }

    // =====================================================
    // GET USER VOTE ON A POLL
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping(
        "/poll/{pollId}/user/{userId}"
    )
    public ResponseEntity<VoteResponse>
            userVote(
                @PathVariable Long pollId,
                @PathVariable Long userId) {

        return ResponseEntity.ok(
                voteService.getUserVote(
                    pollId,
                    userId));
    }

    // =====================================================
    // GET ALL VOTES BY USER
    // SUPER ADMIN + ADMIN + RESIDENT (own)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VoteResponse>>
            userVotes(
                @PathVariable Long userId) {

        return ResponseEntity.ok(
                voteService
                    .getUserVotes(userId));
    }
}