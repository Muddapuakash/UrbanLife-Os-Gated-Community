package com.urbanlife.dto;

import java.time.LocalDateTime;

import com.urbanlife.enums.EventCategory;

import jakarta.validation.constraints.*;

public class EventRequest {

    @NotBlank
    @Size(max = 150)
    private String title;

    @Size(max = 1000)
    private String description;

    @NotNull
    private EventCategory category;

    @NotBlank
    @Size(max = 150)
    private String venue;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    private LocalDateTime registrationDeadline;

    @Positive
    private Integer maxParticipants;

    @NotNull
    private Long communityId;

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

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(
            Long communityId) {
        this.communityId = communityId;
    }
}