package com.urbanlife.entity;

import java.time.LocalDateTime;

import com.urbanlife.enums.EventCategory;
import com.urbanlife.enums.EventStatus;

import jakarta.persistence.*;

@Entity
@Table(
    name = "events",
    indexes = {
        @Index(
            name = "idx_event_community",
            columnList = "community_id"
        ),
        @Index(
            name = "idx_event_start_time",
            columnList = "start_time"
        )
    }
)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EventCategory category;

    @Column(nullable = false, length = 150)
    private String venue;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    private LocalDateTime registrationDeadline;

    /*
     * null = unlimited registrations
     */
    private Integer maxParticipants;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "community_id",
        nullable = false
    )
    private Community community;

    @Column(
        nullable = false,
        updatable = false
    )
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Event() {
    }

    @PrePersist
    public void onCreate() {

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (status == null) {
            status = EventStatus.DRAFT;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {
        this.description = description;
    }

    public EventCategory getCategory() {
        return category;
    }

    public void setCategory(
            EventCategory category) {
        this.category = category;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(
            LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(
            LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(
            LocalDateTime registrationDeadline) {
        this.registrationDeadline =
                registrationDeadline;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(
            Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(
            EventStatus status) {
        this.status = status;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(
            Community community) {
        this.community = community;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}