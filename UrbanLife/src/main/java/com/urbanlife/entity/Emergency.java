package com.urbanlife.entity;

import java.time.LocalDateTime;

import com.urbanlife.enums.EmergencyPriority;
import com.urbanlife.enums.EmergencyStatus;
import com.urbanlife.enums.EmergencyType;

import jakarta.persistence.*;

@Entity
@Table(name = "emergencies")
public class Emergency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emergencyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flat_id", nullable = false)
    private Flat flat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmergencyType emergencyType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmergencyPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmergencyStatus status;

    @Column(nullable = false, length = 1000)
    private String description;

    /*
     * Example:
     * Flat A-101
     * Parking Basement
     * Club House
     * Main Gate
     */
    @Column(length = 300)
    private String locationDetails;

    /*
     * User/admin/security ID of responder.
     * We'll keep this as User because responder
     * may be ADMIN or SECURITY.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responder_id")
    private User responder;

    private LocalDateTime acknowledgedAt;

    private LocalDateTime responseStartedAt;

    private LocalDateTime resolvedAt;

    @Column(length = 1000)
    private String resolutionNotes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Emergency() {
    }

    @PrePersist
    public void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = EmergencyStatus.OPEN;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(Long emergencyId) {
        this.emergencyId = emergencyId;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public Flat getFlat() {
        return flat;
    }

    public void setFlat(Flat flat) {
        this.flat = flat;
    }

    public EmergencyType getEmergencyType() {
        return emergencyType;
    }

    public void setEmergencyType(EmergencyType emergencyType) {
        this.emergencyType = emergencyType;
    }

    public EmergencyPriority getPriority() {
        return priority;
    }

    public void setPriority(EmergencyPriority priority) {
        this.priority = priority;
    }

    public EmergencyStatus getStatus() {
        return status;
    }

    public void setStatus(EmergencyStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }

    public User getResponder() {
        return responder;
    }

    public void setResponder(User responder) {
        this.responder = responder;
    }

    public LocalDateTime getAcknowledgedAt() {
        return acknowledgedAt;
    }

    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }

    public LocalDateTime getResponseStartedAt() {
        return responseStartedAt;
    }

    public void setResponseStartedAt(LocalDateTime responseStartedAt) {
        this.responseStartedAt = responseStartedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}