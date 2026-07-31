package com.urbanlife.dto;

import java.time.LocalDateTime;

import com.urbanlife.enums.NoticePriority;
import com.urbanlife.enums.NoticeStatus;
import com.urbanlife.enums.NoticeTargetType;
import com.urbanlife.enums.NoticeType;

public class NoticeResponse {

    private Long noticeId;

    private String title;
    private String message;

    private NoticeType noticeType;
    private NoticePriority priority;
    private NoticeTargetType targetType;
    private NoticeStatus status;

    private Long communityId;
    private String communityName;

    private Long blockId;
    private String blockName;

    private LocalDateTime publishedAt;
    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
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

    public NoticeStatus getStatus() {
        return status;
    }

    public void setStatus(NoticeStatus status) {
        this.status = status;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public Long getBlockId() {
        return blockId;
    }

    public void setBlockId(Long blockId) {
        this.blockId = blockId;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}