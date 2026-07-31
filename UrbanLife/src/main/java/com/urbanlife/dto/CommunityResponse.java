package com.urbanlife.dto;

import java.time.LocalDateTime;

import com.urbanlife.enums.CommunityStatus;

public class CommunityResponse {

    private Long communityId;
    private String name;
    private String registrationNumber;
    private String email;
    private String phone;
    private String addressLine;
    private String city;
    private String state;
    private String pincode;
    private CommunityStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommunityResponse() {
    }

    public CommunityResponse(
            Long communityId,
            String name,
            String registrationNumber,
            String email,
            String phone,
            String addressLine,
            String city,
            String state,
            String pincode,
            CommunityStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.communityId = communityId;
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.email = email;
        this.phone = phone;
        this.addressLine = addressLine;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public String getName() {
        return name;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPincode() {
        return pincode;
    }

    public CommunityStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}