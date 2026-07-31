package com.urbanlife.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.urbanlife.enums.ResidentStatus;
import com.urbanlife.enums.ResidentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "residents")
public class Resident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long residentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        unique = true
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "flat_id",
        nullable = false
    )
    private Flat flat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResidentType residentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResidentStatus status;

    @Column(nullable = false)
    private Boolean primaryResident;

    @Column(nullable = false)
    private LocalDate moveInDate;

    private LocalDate moveOutDate;

    @Column(length = 100)
    private String emergencyContactName;

    @Column(length = 15)
    private String emergencyContactPhone;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Resident() {
    }

    @PrePersist
    public void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = ResidentStatus.ACTIVE;
        }

        if (primaryResident == null) {
            primaryResident = false;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Flat getFlat() {
        return flat;
    }

    public void setFlat(Flat flat) {
        this.flat = flat;
    }

    public ResidentType getResidentType() {
        return residentType;
    }

    public void setResidentType(ResidentType residentType) {
        this.residentType = residentType;
    }

    public ResidentStatus getStatus() {
        return status;
    }

    public void setStatus(ResidentStatus status) {
        this.status = status;
    }

    public Boolean getPrimaryResident() {
        return primaryResident;
    }

    public void setPrimaryResident(Boolean primaryResident) {
        this.primaryResident = primaryResident;
    }

    public LocalDate getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(LocalDate moveInDate) {
        this.moveInDate = moveInDate;
    }

    public LocalDate getMoveOutDate() {
        return moveOutDate;
    }

    public void setMoveOutDate(LocalDate moveOutDate) {
        this.moveOutDate = moveOutDate;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(
            String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(
            String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}