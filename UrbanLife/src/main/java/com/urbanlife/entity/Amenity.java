package com.urbanlife.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.urbanlife.enums.AmenityStatus;
import com.urbanlife.enums.AmenityType;

import jakarta.persistence.*;

@Entity
@Table(
    name = "amenities",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"community_id", "name"}
        )
    }
)
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long amenityId;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AmenityType amenityType;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private LocalTime openingTime;

    @Column(nullable = false)
    private LocalTime closingTime;

    @Column(nullable = false)
    private Integer maxBookingHours;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AmenityStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Amenity() {
    }

    @PrePersist
    public void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = AmenityStatus.AVAILABLE;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(Long amenityId) {
        this.amenityId = amenityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AmenityType getAmenityType() {
        return amenityType;
    }

    public void setAmenityType(AmenityType amenityType) {
        this.amenityType = amenityType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    public Integer getMaxBookingHours() {
        return maxBookingHours;
    }

    public void setMaxBookingHours(Integer maxBookingHours) {
        this.maxBookingHours = maxBookingHours;
    }

    public AmenityStatus getStatus() {
        return status;
    }

    public void setStatus(AmenityStatus status) {
        this.status = status;
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