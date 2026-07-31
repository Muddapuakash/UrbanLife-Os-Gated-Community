package com.urbanlife.dto;

import com.urbanlife.enums.ParkingSlotType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateParkingSlotRequest {

    @NotNull(message = "Community id is required")
    private Long communityId;

    @NotBlank(message = "Slot number is required")
    private String slotNumber;

    @NotNull(message = "Slot type is required")
    private ParkingSlotType slotType;

    private String locationDescription;

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
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

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(
            String locationDescription) {
        this.locationDescription = locationDescription;
    }
}