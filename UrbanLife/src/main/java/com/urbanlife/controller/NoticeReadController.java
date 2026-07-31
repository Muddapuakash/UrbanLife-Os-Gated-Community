package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.urbanlife.dto.NoticeReadResponse;
import com.urbanlife.service.NoticeReadService;

@RestController
@RequestMapping("/api/v1/notice-reads")
public class NoticeReadController {

    private final NoticeReadService readService;

    public NoticeReadController(
            NoticeReadService readService) {

        this.readService = readService;
    }

    // =====================================================
    // MARK NOTICE AS READ
    // ALL AUTHENTICATED ROLES
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @PostMapping(
        "/notice/{noticeId}/resident/{residentId}"
    )
    public ResponseEntity<NoticeReadResponse>
            markAsRead(
                    @PathVariable Long noticeId,
                    @PathVariable Long residentId) {

        return ResponseEntity.ok(
                readService.markAsRead(
                    noticeId,
                    residentId));
    }

    // =====================================================
    // GET READERS OF A NOTICE
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/notice/{noticeId}")
    public ResponseEntity<List<NoticeReadResponse>>
            getReaders(
                    @PathVariable Long noticeId) {

        return ResponseEntity.ok(
                readService.getReaders(
                    noticeId));
    }

    // =====================================================
    // GET RESIDENT READ HISTORY
    // SUPER ADMIN + ADMIN + RESIDENT (own)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/resident/{residentId}")
    public ResponseEntity<List<NoticeReadResponse>>
            getResidentHistory(
                    @PathVariable Long residentId) {

        return ResponseEntity.ok(
                readService
                    .getResidentReadHistory(
                        residentId));
    }

    // =====================================================
    // GET READ COUNT FOR A NOTICE
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/notice/{noticeId}/count")
    public ResponseEntity<Long> getReadCount(
            @PathVariable Long noticeId) {

        return ResponseEntity.ok(
                readService.getReadCount(
                    noticeId));
    }
}