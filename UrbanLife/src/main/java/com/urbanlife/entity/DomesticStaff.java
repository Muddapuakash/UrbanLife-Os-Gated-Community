package com.urbanlife.entity;

import java.time.LocalDateTime;

import com.urbanlife.enums.StaffStatus;
import com.urbanlife.enums.StaffType;
import com.urbanlife.enums.VerificationStatus;

import jakarta.persistence.*;

@Entity
@Table(
    name = "domestic_staff",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_domestic_staff_verification_ref",
            columnNames = "verification_reference"
        )
    }
)
public class DomesticStaff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long staffId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StaffType staffType;

    @Column(length = 100)
    private String customStaffType;

    @Column(length = 500)
    private String address;

    @Column(length = 500)
    private String photoUrl;

    /*
     * Store Aadhaar / national ID reference for identity verification.
     * Must be unique across the entire platform.
     */
    @Column(length = 100, unique = true)
    private String verificationReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus verificationStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StaffStatus status;

    @Column(length = 500)
    private String verificationRemarks;

    @Column(length = 500)
    private String blockedReason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public DomesticStaff() {
    }

    @PrePersist
    public void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (verificationStatus == null) {
            verificationStatus = VerificationStatus.PENDING;
        }

        if (status == null) {
            status = StaffStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
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

    public void setVerificationStatus(
            VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public StaffStatus getStatus() {
        return status;
    }

    public void setStatus(StaffStatus status) {
        this.status = status;
    }

    public String getVerificationRemarks() {
        return verificationRemarks;
    }

    public void setVerificationRemarks(String verificationRemarks) {
        this.verificationRemarks = verificationRemarks;
    }

    public String getBlockedReason() {
        return blockedReason;
    }

    public void setBlockedReason(String blockedReason) {
        this.blockedReason = blockedReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}