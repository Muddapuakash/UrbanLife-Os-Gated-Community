package com.urbanlife.dto;

import java.time.LocalTime;

import com.urbanlife.enums.AmenityType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateAmenityRequest {

    @NotNull(message = "Community id is required")
    private Long communityId;

    @NotBlank(message = "Amenity name is required")
    private String name;

    @NotNull(message = "Amenity type is required")
    private AmenityType amenityType;

    @Size(max = 500)
    private String description;

    @NotNull
    @Min(1)
    private Integer capacity;

    @NotNull
    private LocalTime openingTime;

    @NotNull
    private LocalTime closingTime;

    @NotNull
    @Min(1)
    private Integer maxBookingHours;

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
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
}