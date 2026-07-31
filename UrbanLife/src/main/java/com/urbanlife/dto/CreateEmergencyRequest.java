package com.urbanlife.dto;

import com.urbanlife.enums.EmergencyPriority;
import com.urbanlife.enums.EmergencyType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateEmergencyRequest {

    @NotNull
    private Long residentId;

    @NotNull
    private EmergencyType emergencyType;

    @NotNull
    private EmergencyPriority priority;

    @NotBlank
    @Size(max = 1000)
    private String description;

    @Size(max = 300)
    private String locationDetails;

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
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
}