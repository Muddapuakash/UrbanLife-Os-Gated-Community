package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.urbanlife.dto.LostFoundItemRequest;
import com.urbanlife.dto.LostFoundItemResponse;
import com.urbanlife.enums.ItemCategory;
import com.urbanlife.enums.ItemReportType;
import com.urbanlife.service.LostFoundService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/lost-found")
public class LostFoundController {

    private final LostFoundService lostFoundService;

    public LostFoundController(
            LostFoundService lostFoundService) {

        this.lostFoundService =
                lostFoundService;
    }

    // =====================================================
    // REPORT ITEM (lost or found)
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @PostMapping
    public ResponseEntity<LostFoundItemResponse>
            reportItem(
                @Valid @RequestBody
                LostFoundItemRequest request) {

        return new ResponseEntity<>(
                lostFoundService.reportItem(request),
                HttpStatus.CREATED);
    }

    // =====================================================
    // GET ITEM BY ID
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/{itemId}")
    public ResponseEntity<LostFoundItemResponse>
            getItem(
                @PathVariable Long itemId) {

        return ResponseEntity.ok(
                lostFoundService.getItem(itemId));
    }

    // =====================================================
    // GET COMMUNITY ITEMS
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/community/{communityId}")
    public ResponseEntity<
            List<LostFoundItemResponse>>
            communityItems(
                @PathVariable Long communityId) {

        return ResponseEntity.ok(
                lostFoundService
                    .getCommunityItems(
                        communityId));
    }

    // =====================================================
    // GET OPEN ITEMS BY TYPE
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping(
        "/community/{communityId}/open"
    )
    public ResponseEntity<
            List<LostFoundItemResponse>>
            openItems(
                @PathVariable Long communityId,
                @RequestParam
                ItemReportType type) {

        return ResponseEntity.ok(
                lostFoundService
                    .getOpenItems(
                        communityId,
                        type));
    }

    // =====================================================
    // GET ITEMS BY CATEGORY
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping(
        "/community/{communityId}/category"
    )
    public ResponseEntity<
            List<LostFoundItemResponse>>
            byCategory(
                @PathVariable Long communityId,
                @RequestParam
                ItemCategory category) {

        return ResponseEntity.ok(
                lostFoundService
                    .getItemsByCategory(
                        communityId,
                        category));
    }

    // =====================================================
    // GET USER REPORTS
    // ALL AUTHENTICATED ROLES (own)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<
            List<LostFoundItemResponse>>
            userReports(
                @PathVariable Long userId) {

        return ResponseEntity.ok(
                lostFoundService
                    .getUserReports(userId));
    }

    // =====================================================
    // UPDATE ITEM REPORT
    // ALL AUTHENTICATED ROLES (own report)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @PutMapping("/{itemId}/user/{userId}")
    public ResponseEntity<LostFoundItemResponse>
            update(
                @PathVariable Long itemId,
                @PathVariable Long userId,
                @Valid @RequestBody
                LostFoundItemRequest request) {

        return ResponseEntity.ok(
                lostFoundService.updateItem(
                    itemId,
                    userId,
                    request));
    }

    // =====================================================
    // CLOSE / MARK RESOLVED
    // SUPER ADMIN + ADMIN + SECURITY + STAFF
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY', 'STAFF')"
    )
    @PatchMapping("/{itemId}/close")
    public ResponseEntity<LostFoundItemResponse>
            close(
                @PathVariable Long itemId) {

        return ResponseEntity.ok(
                lostFoundService
                    .closeItem(itemId));
    }

    // =====================================================
    // DELETE REPORT
    // SUPER ADMIN + ADMIN + RESIDENT (own)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @DeleteMapping(
        "/{itemId}/user/{userId}"
    )
    public ResponseEntity<Void> delete(
            @PathVariable Long itemId,
            @PathVariable Long userId) {

        lostFoundService.deleteItem(
                itemId,
                userId);

        return ResponseEntity
                .noContent()
                .build();
    }
}