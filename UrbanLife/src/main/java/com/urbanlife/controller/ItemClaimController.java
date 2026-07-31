package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.urbanlife.dto.ItemClaimRequest;
import com.urbanlife.dto.ItemClaimResponse;
import com.urbanlife.service.ItemClaimService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/item-claims")
public class ItemClaimController {

    private final ItemClaimService claimService;

    public ItemClaimController(
            ItemClaimService claimService) {

        this.claimService = claimService;
    }

    // =====================================================
    // CLAIM ITEM
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @PostMapping(
        "/item/{itemId}/user/{userId}"
    )
    public ResponseEntity<ItemClaimResponse>
            claim(
                @PathVariable Long itemId,
                @PathVariable Long userId,
                @Valid @RequestBody
                ItemClaimRequest request) {

        return new ResponseEntity<>(
                claimService.claimItem(
                    itemId,
                    userId,
                    request),
                HttpStatus.CREATED);
    }

    // =====================================================
    // APPROVE CLAIM
    // SUPER ADMIN + ADMIN + SECURITY + STAFF
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY', 'STAFF')"
    )
    @PatchMapping("/{claimId}/approve")
    public ResponseEntity<ItemClaimResponse>
            approve(
                @PathVariable Long claimId) {

        return ResponseEntity.ok(
                claimService
                    .approveClaim(claimId));
    }

    // =====================================================
    // REJECT CLAIM
    // SUPER ADMIN + ADMIN + SECURITY + STAFF
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY', 'STAFF')"
    )
    @PatchMapping("/{claimId}/reject")
    public ResponseEntity<ItemClaimResponse>
            reject(
                @PathVariable Long claimId) {

        return ResponseEntity.ok(
                claimService
                    .rejectClaim(claimId));
    }

    // =====================================================
    // CANCEL CLAIM
    // ALL AUTHENTICATED ROLES (own claim)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @PatchMapping(
        "/{claimId}/cancel/user/{userId}"
    )
    public ResponseEntity<ItemClaimResponse>
            cancel(
                @PathVariable Long claimId,
                @PathVariable Long userId) {

        return ResponseEntity.ok(
                claimService.cancelClaim(
                    claimId,
                    userId));
    }

    // =====================================================
    // MARK ITEM RETURNED
    // SUPER ADMIN + ADMIN + SECURITY + STAFF
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY', 'STAFF')"
    )
    @PatchMapping("/{claimId}/returned")
    public ResponseEntity<ItemClaimResponse>
            returned(
                @PathVariable Long claimId) {

        return ResponseEntity.ok(
                claimService
                    .markItemReturned(
                        claimId));
    }

    // =====================================================
    // GET CLAIMS FOR AN ITEM
    // SUPER ADMIN + ADMIN + SECURITY + STAFF
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/item/{itemId}")
    public ResponseEntity<
            List<ItemClaimResponse>>
            itemClaims(
                @PathVariable Long itemId) {

        return ResponseEntity.ok(
                claimService
                    .getItemClaims(itemId));
    }

    // =====================================================
    // GET USER CLAIMS
    // ALL AUTHENTICATED ROLES (own)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<
            List<ItemClaimResponse>>
            userClaims(
                @PathVariable Long userId) {

        return ResponseEntity.ok(
                claimService
                    .getUserClaims(userId));
    }
}