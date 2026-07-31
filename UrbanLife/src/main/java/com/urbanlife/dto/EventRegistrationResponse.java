package com.urbanlife.dto;

import java.time.LocalDateTime;

import com.urbanlife.enums.RegistrationStatus;

public class EventRegistrationResponse {

    private Long registrationId;

    private Long eventId;
    private String eventTitle;

    private Long userId;

    private RegistrationStatus status;

    private LocalDateTime registeredAt;

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(
            Long registrationId) {
        this.registrationId = registrationId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(
            String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(
            RegistrationStatus status) {
        this.status = status;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(
            LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }
}