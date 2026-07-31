package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.urbanlife.dto.CreateNoticeRequest;
import com.urbanlife.dto.NoticeResponse;
import com.urbanlife.enums.NoticePriority;
import com.urbanlife.enums.NoticeType;
import com.urbanlife.service.NoticeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(
            NoticeService noticeService) {

        this.noticeService = noticeService;
    }

    // =====================================================
    // CREATE NOTICE
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public ResponseEntity<NoticeResponse> create(
            @Valid @RequestBody
            CreateNoticeRequest request) {

        return new ResponseEntity<>(
                noticeService.createNotice(request),
                HttpStatus.CREATED);
    }

    // =====================================================
    // GET ALL NOTICES
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<NoticeResponse>>
            getAll() {

        return ResponseEntity.ok(
                noticeService.getAllNotices());
    }

    // =====================================================
    // GET NOTICE BY ID
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponse> getById(
            @PathVariable Long noticeId) {

        return ResponseEntity.ok(
                noticeService.getNoticeById(
                    noticeId));
    }

    // =====================================================
    // GET NOTICES BY COMMUNITY
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<NoticeResponse>>
            getByCommunity(
                    @PathVariable Long communityId) {

        return ResponseEntity.ok(
                noticeService
                    .getCommunityNotices(
                        communityId));
    }

    // =====================================================
    // GET PUBLISHED NOTICES BY COMMUNITY
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping(
        "/community/{communityId}/published"
    )
    public ResponseEntity<List<NoticeResponse>>
            getPublished(
                    @PathVariable Long communityId) {

        return ResponseEntity.ok(
                noticeService
                    .getPublishedCommunityNotices(
                        communityId));
    }

    // =====================================================
    // GET NOTICES BY TYPE
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping(
        "/community/{communityId}/type"
    )
    public ResponseEntity<List<NoticeResponse>>
            getByType(
                    @PathVariable Long communityId,
                    @RequestParam NoticeType type) {

        return ResponseEntity.ok(
                noticeService.getNoticesByType(
                    communityId,
                    type));
    }

    // =====================================================
    // GET NOTICES BY PRIORITY
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping(
        "/community/{communityId}/priority"
    )
    public ResponseEntity<List<NoticeResponse>>
            getByPriority(
                    @PathVariable Long communityId,
                    @RequestParam
                    NoticePriority priority) {

        return ResponseEntity.ok(
                noticeService
                    .getNoticesByPriority(
                        communityId,
                        priority));
    }

    // =====================================================
    // PUBLISH NOTICE
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PatchMapping("/{noticeId}/publish")
    public ResponseEntity<NoticeResponse> publish(
            @PathVariable Long noticeId) {

        return ResponseEntity.ok(
                noticeService.publishNotice(
                    noticeId));
    }

    // =====================================================
    // CANCEL NOTICE
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PatchMapping("/{noticeId}/cancel")
    public ResponseEntity<NoticeResponse> cancel(
            @PathVariable Long noticeId) {

        return ResponseEntity.ok(
                noticeService.cancelNotice(
                    noticeId));
    }

    // =====================================================
    // EXPIRE NOTICES (system-level batch job)
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PatchMapping("/expire")
    public ResponseEntity<String> expire() {

        int count =
                noticeService.expireNotices();

        return ResponseEntity.ok(
                count
                + " notice(s) marked as expired");
    }
}