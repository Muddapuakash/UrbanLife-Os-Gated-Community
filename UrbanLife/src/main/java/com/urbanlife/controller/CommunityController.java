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

import com.urbanlife.dto.CommunityResponse;
import com.urbanlife.dto.CreateCommunityRequest;
import com.urbanlife.dto.UpdateCommunityRequest;
import com.urbanlife.enums.CommunityStatus;
import com.urbanlife.service.CommunityService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/communities")
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(
            CommunityService communityService) {

        this.communityService = communityService;
    }

    // =====================================================
    // CREATE COMMUNITY
    // SUPER ADMIN ONLY
    // =====================================================

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<CommunityResponse> createCommunity(
            @Valid @RequestBody CreateCommunityRequest request) {

        return new ResponseEntity<>(
                communityService.createCommunity(request),
                HttpStatus.CREATED
        );
    }

    // =====================================================
    // GET COMMUNITY BY ID
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN')"
    )
    @GetMapping("/{communityId}")
    public ResponseEntity<CommunityResponse> getCommunityById(
            @PathVariable Long communityId) {

        return ResponseEntity.ok(
                communityService.getCommunityById(communityId)
        );
    }

    // =====================================================
    // GET ALL COMMUNITIES
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN')"
    )
    @GetMapping
    public ResponseEntity<List<CommunityResponse>>
            getAllCommunities() {

        return ResponseEntity.ok(
                communityService.getAllCommunities()
        );
    }

    // =====================================================
    // UPDATE COMMUNITY
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN')"
    )
    @PutMapping("/{communityId}")
    public ResponseEntity<CommunityResponse> updateCommunity(
            @PathVariable Long communityId,
            @Valid @RequestBody UpdateCommunityRequest request) {

        return ResponseEntity.ok(
                communityService.updateCommunity(
                        communityId,
                        request)
        );
    }

    // =====================================================
    // DELETE COMMUNITY
    // SUPER ADMIN ONLY
    // =====================================================

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{communityId}")
    public ResponseEntity<Void> deleteCommunity(
            @PathVariable Long communityId) {

        communityService.deleteCommunity(communityId);

        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // SEARCH COMMUNITY BY CITY
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN')"
    )
    @GetMapping("/search/city")
    public ResponseEntity<List<CommunityResponse>>
            getCommunitiesByCity(
                    @RequestParam String city) {

        return ResponseEntity.ok(
                communityService.getCommunitiesByCity(city)
        );
    }

    // =====================================================
    // SEARCH COMMUNITY BY STATUS
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN')"
    )
    @GetMapping("/search/status")
    public ResponseEntity<List<CommunityResponse>>
            getCommunitiesByStatus(
                    @RequestParam CommunityStatus status) {

        return ResponseEntity.ok(
                communityService.getCommunitiesByStatus(status)
        );
    }
}