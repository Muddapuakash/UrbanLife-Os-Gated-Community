package com.urbanlife.entity;

import java.time.LocalDateTime;

import com.urbanlife.enums.NoticePriority;
import com.urbanlife.enums.NoticeStatus;
import com.urbanlife.enums.NoticeTargetType;
import com.urbanlife.enums.NoticeType;

import jakarta.persistence.*;

@Entity
@Table(name = "notices")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 3000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeType noticeType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticePriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeTargetType targetType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    /*
     * Null when targetType = COMMUNITY.
     * Required when targetType = BLOCK.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id")
    private Block block;

    private LocalDateTime publishedAt;

    private LocalDateTime expiresAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Notice() {
    }

    @PrePersist
    public void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = NoticeStatus.DRAFT;
        }

        if (priority == null) {
            priority = NoticePriority.NORMAL;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

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

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}