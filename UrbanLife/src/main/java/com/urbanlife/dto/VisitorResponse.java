package com.urbanlife.dto;

import java.time.LocalDateTime;

import com.urbanlife.enums.ApprovalType;
import com.urbanlife.enums.VisitStatus;
import com.urbanlife.enums.VisitorType;

public class VisitorResponse {

    private Long visitorId;

    private String visitorName;
    private String phone;

    private VisitorType visitorType;
    private ApprovalType approvalType;
    private VisitStatus status;

    private String vehicleNumber;
    private String purpose;
    private String passCode;

    private Long residentId;
    private String residentName;

    private Long flatId;
    private String flatNumber;

    private Long blockId;
    private String blockName;

    private Long communityId;
    private String communityName;

    private LocalDateTime expectedArrival;
    private LocalDateTime validUntil;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    private Long checkedInByUserId;
    private String checkedInByName;

    private Long checkedOutByUserId;
    private String checkedOutByName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VisitorResponse() {
    }

    // Generate getters and setters from IDE

    public Long getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(Long visitorId) {
        this.visitorId = visitorId;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public VisitorType getVisitorType() {
        return visitorType;
    }

    public void setVisitorType(VisitorType visitorType) {
        this.visitorType = visitorType;
    }

    public ApprovalType getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(ApprovalType approvalType) {
        this.approvalType = approvalType;
    }

    public VisitStatus getStatus() {
        return status;
    }

    public void setStatus(VisitStatus status) {
        this.status = status;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getPassCode() {
        return passCode;
    }

    public void setPassCode(String passCode) {
        this.passCode = passCode;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public String getResidentName() {
        return residentName;
    }

    public void setResidentName(String residentName) {
        this.residentName = residentName;
    }

    public Long getFlatId() {
        return flatId;
    }

    public void setFlatId(Long flatId) {
        this.flatId = flatId;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }

    public Long getBlockId() {
        return blockId;
    }

    public void setBlockId(Long blockId) {
        this.blockId = blockId;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
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

    public LocalDateTime getExpectedArrival() {
        return expectedArrival;
    }

    public void setExpectedArrival(LocalDateTime expectedArrival) {
        this.expectedArrival = expectedArrival;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public Long getCheckedInByUserId() {
        return checkedInByUserId;
    }

    public void setCheckedInByUserId(Long checkedInByUserId) {
        this.checkedInByUserId = checkedInByUserId;
    }

    public String getCheckedInByName() {
        return checkedInByName;
    }

    public void setCheckedInByName(String checkedInByName) {
        this.checkedInByName = checkedInByName;
    }

    public Long getCheckedOutByUserId() {
        return checkedOutByUserId;
    }

    public void setCheckedOutByUserId(Long checkedOutByUserId) {
        this.checkedOutByUserId = checkedOutByUserId;
    }

    public String getCheckedOutByName() {
        return checkedOutByName;
    }

    public void setCheckedOutByName(String checkedOutByName) {
        this.checkedOutByName = checkedOutByName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}