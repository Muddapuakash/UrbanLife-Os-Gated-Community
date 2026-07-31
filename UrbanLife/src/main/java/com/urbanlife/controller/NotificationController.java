package com.urbanlife.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.urbanlife.dto.NotificationRequest;
import com.urbanlife.dto.NotificationResponse;
import com.urbanlife.service.AsyncNotificationService;
import com.urbanlife.service.NotificationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    private final AsyncNotificationService asyncNotificationService;

    public NotificationController(
            NotificationService notificationService,
            AsyncNotificationService asyncNotificationService) {

        this.notificationService = notificationService;
        this.asyncNotificationService = asyncNotificationService;
    }

    // =====================================================
    // 1. CREATE NOTIFICATION
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public ResponseEntity<NotificationResponse> create(
            @Valid @RequestBody NotificationRequest request) {

        return new ResponseEntity<>(
                notificationService.createNotification(request),
                HttpStatus.CREATED);
    }

    // =====================================================
    // 2. GET NOTIFICATION BY ID
    // ALL AUTHENTICATED ROLES (own notifications)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse> get(
            @PathVariable Long notificationId) {

        return ResponseEntity.ok(
                notificationService.getNotification(notificationId));
    }

    // =====================================================
    // 3. GET ALL NOTIFICATIONS OF USER
    // ALL AUTHENTICATED ROLES (own)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>>
            getUserNotifications(
                    @PathVariable Long userId) {

        return ResponseEntity.ok(
                notificationService.getUserNotifications(userId));
    }

    // =====================================================
    // 4. GET UNREAD NOTIFICATIONS
    // ALL AUTHENTICATED ROLES (own)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponse>>
            getUnread(
                    @PathVariable Long userId) {

        return ResponseEntity.ok(
                notificationService.getUnreadNotifications(userId));
    }

    // =====================================================
    // 5. GET NOTIFICATIONS BY TYPE
    // ALL AUTHENTICATED ROLES (own)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<List<NotificationResponse>>
            getByType(
                    @PathVariable Long userId,
                    @PathVariable String type) {

        return ResponseEntity.ok(
                notificationService.getNotificationsByType(
                        userId,
                        com.urbanlife.enums.NotificationType.valueOf(type)));
    }

    // =====================================================
    // 6. GET UNREAD COUNT
    // ALL AUTHENTICATED ROLES (own)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount(
            @PathVariable Long userId) {

        long count =
                notificationService.getUnreadCount(userId);

        return ResponseEntity.ok(
                Map.of("unreadCount", count));
    }

    // =====================================================
    // 7. MARK ONE NOTIFICATION AS READ
    // ALL AUTHENTICATED ROLES (own)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @PatchMapping("/{notificationId}/read/user/{userId}")
    public ResponseEntity<NotificationResponse> markRead(
            @PathVariable Long notificationId,
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                notificationService.markAsRead(
                        notificationId,
                        userId));
    }

    // =====================================================
    // 8. MARK ALL NOTIFICATIONS AS READ
    // ALL AUTHENTICATED ROLES (own)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @PatchMapping("/user/{userId}/read-all")
    public ResponseEntity<Map<String, Integer>> markAllRead(
            @PathVariable Long userId) {

        int count =
                notificationService.markAllAsRead(userId);

        return ResponseEntity.ok(
                Map.of("updatedCount", count));
    }

    // =====================================================
    // 9. DELETE NOTIFICATION
    // ALL AUTHENTICATED ROLES (own)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF')"
    )
    @DeleteMapping("/{notificationId}/user/{userId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long notificationId,
            @PathVariable Long userId) {

        notificationService.deleteNotification(
                notificationId,
                userId);

        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // 10. SEND COMMUNITY NOTIFICATION (bulk broadcast)
    // SUPER ADMIN + ADMIN ONLY
    // NOTE: test-async endpoints removed for security
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping("/community/{communityId}/broadcast")
    public ResponseEntity<String> broadcastToCommunity(
            @PathVariable Long communityId,
            @Valid @RequestBody NotificationRequest request) {

        asyncNotificationService.sendCommunityNotificationAsync(
                communityId,
                request.getTitle(),
                request.getMessage(),
                request.getType(),
                request.getPriority(),
                request.getReferenceType(),
                request.getReferenceId());

        return ResponseEntity.ok(
                "Community notification broadcast started");
    }
}