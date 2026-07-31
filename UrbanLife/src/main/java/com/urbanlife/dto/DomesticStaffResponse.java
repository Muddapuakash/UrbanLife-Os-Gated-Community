package com.urbanlife.dto;

import java.time.LocalDateTime;

import com.urbanlife.enums.StaffStatus;
import com.urbanlife.enums.StaffType;
import com.urbanlife.enums.VerificationStatus;

public class DomesticStaffResponse {

    private Long staffId;
    private Long communityId;
    private String communityName;

    private String name;
    private String phone;

    private StaffType staffType;
    private String customStaffType;

    private String address;
    private String photoUrl;

    private String verificationReference;

    private VerificationStatus verificationStatus;
    private String verificationRemarks;

    private StaffStatus status;
    private String blockedReason;

    private Double averageRating;

    private LocalDateTime createdAt;

    public DomesticStaffResponse() {
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public StaffType getStaffType() {
        return staffType;
    }

    public void setStaffType(StaffType staffType) {
        this.staffType = staffType;
    }

    public String getCustomStaffType() {
        return customStaffType;
    }

    public void setCustomStaffType(String customStaffType) {
        this.customStaffType = customStaffType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getVerificationReference() {
        return verificationReference;
    }

    public void setVerificationReference(String verificationReference) {
        this.verificationReference = verificationReference;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getVerificationRemarks() {
        return verificationRemarks;
    }

    public void setVerificationRemarks(String verificationRemarks) {
        this.verificationRemarks = verificationRemarks;
    }

    public StaffStatus getStatus() {
        return status;
    }

    public void setStatus(StaffStatus status) {
        this.status = status;
    }

    public String getBlockedReason() {
        return blockedReason;
    }

    public void setBlockedReason(String blockedReason) {
        this.blockedReason = blockedReason;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}