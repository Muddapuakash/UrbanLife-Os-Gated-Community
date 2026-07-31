package com.urbanlife.dto;

import java.time.LocalDate;

import com.urbanlife.enums.ResidentType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CreateResidentRequest {

    @NotNull(message = "User id is required")
    private Long userId;

    @NotNull(message = "Flat id is required")
    private Long flatId;

    @NotNull(message = "Resident type is required")
    private ResidentType residentType;

    @NotNull(message = "Primary resident value is required")
    private Boolean primaryResident;

    @NotNull(message = "Move-in date is required")
    private LocalDate moveInDate;

    private String emergencyContactName;

    @Pattern(
        regexp = "^[6-9][0-9]{9}$",
        message = "Enter a valid emergency contact number"
    )
    private String emergencyContactPhone;

    public CreateResidentRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFlatId() {
        return flatId;
    }

    public void setFlatId(Long flatId) {
        this.flatId = flatId;
    }

    public ResidentType getResidentType() {
        return residentType;
    }

    public void setResidentType(ResidentType residentType) {
        this.residentType = residentType;
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

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }
}