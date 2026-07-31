package com.urbanlife.entity;

import java.time.LocalDateTime;

import com.urbanlife.enums.NotificationPriority;
import com.urbanlife.enums.NotificationType;

import jakarta.persistence.*;

@Entity
@Table(
    name = "notifications",
    indexes = {
        @Index(
            name = "idx_notification_user_read",
            columnList = "user_id,is_read"
        ),
        @Index(
            name = "idx_notification_community",
            columnList = "community_id"
        ),
        @Index(
            name = "idx_notification_created",
            columnList = "created_at"
        )
    }
)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 1500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    /*
     * Optional reference to another module.
     *
     * Example:
     * referenceType = "POLL"
     * referenceId = 10
     */
    @Column(length = 50)
    private String referenceType;

    private Long referenceId;

    @Column(nullable = false)
    private boolean isRead;

    private LocalDateTime readAt;

    @Column(
        nullable = false,
        updatable = false
    )
    private LocalDateTime createdAt;

    public Notification() {
    }

    @PrePersist
    public void onCreate() {

        createdAt = LocalDateTime.now();

        if (priority == null) {
            priority = NotificationPriority.NORMAL;
        }

        isRead = false;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
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

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationPriority getPriority() {
        return priority;
    }

    public void setPriority(
            NotificationPriority priority) {
        this.priority = priority;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(
            Community community) {
        this.community = community;
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
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
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
}