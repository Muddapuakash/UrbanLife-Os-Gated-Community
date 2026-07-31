package com.urbanlife.entity;

import java.time.LocalDateTime;

import com.urbanlife.enums.ParkingSlotStatus;
import com.urbanlife.enums.ParkingSlotType;

import jakarta.persistence.*;

@Entity
@Table(
    name = "parking_slots",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"community_id", "slot_number"}
        )
    }
)
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long parkingSlotId;

    @Column(nullable = false, length = 30)
    private String slotNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParkingSlotType slotType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParkingSlotStatus status;

    @Column(length = 100)
    private String locationDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public ParkingSlot() {
    }

    @PrePersist
    public void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = ParkingSlotStatus.AVAILABLE;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getParkingSlotId() {
        return parkingSlotId;
    }

    public void setParkingSlotId(Long parkingSlotId) {
        this.parkingSlotId = parkingSlotId;
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(String slotNumber) {
        this.slotNumber = slotNumber;
    }

    public ParkingSlotType getSlotType() {
        return slotType;
    }

    public void setSlotType(ParkingSlotType slotType) {
        this.slotType = slotType;
    }

    public ParkingSlotStatus getStatus() {
        return status;
    }

    public void setStatus(ParkingSlotStatus status) {
        this.status = status;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(
            String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}