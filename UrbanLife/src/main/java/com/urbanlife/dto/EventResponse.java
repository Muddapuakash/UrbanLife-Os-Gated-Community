package com.urbanlife.dto;

import java.time.LocalDateTime;

import com.urbanlife.enums.EventCategory;
import com.urbanlife.enums.EventStatus;

public class EventResponse {

    private Long eventId;

    private String title;
    private String description;

    private EventCategory category;

    private String venue;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private LocalDateTime registrationDeadline;

    private Integer maxParticipants;

    private Long registeredParticipants;

    private EventStatus status;

    private Long communityId;

    private LocalDateTime createdAt;

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

    public Long getRegisteredParticipants() {
        return registeredParticipants;
    }

    public void setRegisteredParticipants(
            Long registeredParticipants) {
        this.registeredParticipants =
                registeredParticipants;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(
            EventStatus status) {
        this.status = status;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(
            Long communityId) {
        this.communityId = communityId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}