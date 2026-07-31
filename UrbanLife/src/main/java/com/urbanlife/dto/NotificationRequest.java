package com.urbanlife.dto;

import com.urbanlife.enums.NotificationPriority;
import com.urbanlife.enums.NotificationType;

import jakarta.validation.constraints.*;

public class NotificationRequest {

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    @Size(max = 1500)
    private String message;

    @NotNull
    private NotificationType type;

    private NotificationPriority priority;

    @NotNull
    private Long userId;

    private Long communityId;

    @Size(max = 50)
    private String referenceType;

    private Long referenceId;

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
}