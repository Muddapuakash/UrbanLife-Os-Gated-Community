package com.urbanlife.dto;

import com.urbanlife.enums.StaffType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateDomesticStaffRequest {

    @NotNull
    private Long communityId;

    @NotBlank
    @Size(max = 150)
    private String name;

    @NotBlank
    @Size(max = 20)
    private String phone;

    @NotNull
    private StaffType staffType;

    @Size(max = 100)
    private String customStaffType;

    @Size(max = 500)
    private String address;

    @Size(max = 500)
    private String photoUrl;

    @NotBlank(message = "Aadhaar / verification ID is required")
    @Size(max = 100, message = "Verification ID cannot exceed 100 characters")
    @Pattern(
        regexp = "^[0-9]{4}[-]?[0-9]{4}[-]?[0-9]{4}$",
        message = "Enter a valid 12-digit Aadhaar number (e.g. 1234-5678-9012)"
    )
    private String verificationReference;

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
}