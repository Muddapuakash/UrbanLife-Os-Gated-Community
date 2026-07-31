package com.urbanlife.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.urbanlife.enums.ResidentStatus;
import com.urbanlife.enums.ResidentType;

public class ResidentResponse {

    private Long residentId;

    private Long userId;
    private String residentName;
    private String email;
    private String phone;

    private ResidentType residentType;
    private ResidentStatus status;
    private Boolean primaryResident;

    private Long flatId;
    private String flatNumber;

    private Long blockId;
    private String blockName;

    private Long communityId;
    private String communityName;

    private LocalDate moveInDate;
    private LocalDate moveOutDate;

    private String emergencyContactName;
    private String emergencyContactPhone;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ResidentResponse() {
    }

    public ResidentResponse(
            Long residentId,
            Long userId,
            String residentName,
            String email,
            String phone,
            ResidentType residentType,
            ResidentStatus status,
            Boolean primaryResident,
            Long flatId,
            String flatNumber,
            Long blockId,
            String blockName,
            Long communityId,
            String communityName,
            LocalDate moveInDate,
            LocalDate moveOutDate,
            String emergencyContactName,
            String emergencyContactPhone,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.residentId = residentId;
        this.userId = userId;
        this.residentName = residentName;
        this.email = email;
        this.phone = phone;
        this.residentType = residentType;
        this.status = status;
        this.primaryResident = primaryResident;
        this.flatId = flatId;
        this.flatNumber = flatNumber;
        this.blockId = blockId;
        this.blockName = blockName;
        this.communityId = communityId;
        this.communityName = communityName;
        this.moveInDate = moveInDate;
        this.moveOutDate = moveOutDate;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getResidentId() {
        return residentId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getResidentName() {
        return residentName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public ResidentType getResidentType() {
        return residentType;
    }

    public ResidentStatus getStatus() {
        return status;
    }

    public Boolean getPrimaryResident() {
        return primaryResident;
    }

    public Long getFlatId() {
        return flatId;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public Long getBlockId() {
        return blockId;
    }

    public String getBlockName() {
        return blockName;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public LocalDate getMoveInDate() {
        return moveInDate;
    }

    public LocalDate getMoveOutDate() {
        return moveOutDate;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}