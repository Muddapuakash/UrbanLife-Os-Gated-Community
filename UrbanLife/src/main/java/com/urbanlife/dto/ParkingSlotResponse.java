package com.urbanlife.dto;

import com.urbanlife.enums.ParkingSlotStatus;
import com.urbanlife.enums.ParkingSlotType;

public class ParkingSlotResponse {

    private Long parkingSlotId;
    private String slotNumber;

    private ParkingSlotType slotType;
    private ParkingSlotStatus status;

    private String locationDescription;

    private Long communityId;
    private String communityName;

    public ParkingSlotResponse() {
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

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }
}