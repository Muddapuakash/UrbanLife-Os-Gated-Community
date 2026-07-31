package com.urbanlife.dto;

import java.time.LocalDateTime;

import com.urbanlife.enums.NoticePriority;
import com.urbanlife.enums.NoticeTargetType;
import com.urbanlife.enums.NoticeType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateNoticeRequest {

    @NotNull
    private Long communityId;

    private Long blockId;

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    @Size(max = 3000)
    private String message;

    @NotNull
    private NoticeType noticeType;

    @NotNull
    private NoticePriority priority;

    @NotNull
    private NoticeTargetType targetType;

    private LocalDateTime expiresAt;

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Long getBlockId() {
        return blockId;
    }

    public void setBlockId(Long blockId) {
        this.blockId = blockId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NoticeType getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(NoticeType noticeType) {
        this.noticeType = noticeType;
    }

    public NoticePriority getPriority() {
        return priority;
    }

    public void setPriority(NoticePriority priority) {
        this.priority = priority;
    }

    public NoticeTargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(NoticeTargetType targetType) {
        this.targetType = targetType;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}