package com.urbanlife.dto;

import java.time.LocalDateTime;

import com.urbanlife.enums.NotificationPriority;
import com.urbanlife.enums.NotificationType;

public class NotificationResponse {

    private Long notificationId;

    private String title;
    private String message;

    private NotificationType type;
    private NotificationPriority priority;

    private Long userId;
    private Long communityId;

    private String referenceType;
    private Long referenceId;

    private boolean read;

    private LocalDateTime readAt;
    private LocalDateTime createdAt;

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(
            Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(
            String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(
            String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(
            NotificationType type) {
        this.type = type;
    }

    public NotificationPriority getPriority() {
        return priority;
    }

    public void setPriority(
            NotificationPriority priority) {
        this.priority = priority;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(
            Long userId) {
        this.userId = userId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(
            Long communityId) {
        this.communityId = communityId;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(
            String referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(
            Long referenceId) {
        this.referenceId = referenceId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(
            LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}