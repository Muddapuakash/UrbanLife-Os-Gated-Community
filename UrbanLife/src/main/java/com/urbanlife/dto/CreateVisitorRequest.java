package com.urbanlife.dto;

import java.time.LocalDateTime;

import com.urbanlife.enums.ApprovalType;
import com.urbanlife.enums.VisitorType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateVisitorRequest {

    @NotNull(message = "Resident id is required")
    private Long residentId;

    @NotBlank(message = "Visitor name is required")
    @Size(max = 100)
    private String visitorName;

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^[6-9][0-9]{9}$",
        message = "Enter a valid phone number"
    )
    private String phone;

    @NotNull(message = "Visitor type is required")
    private VisitorType visitorType;

    @NotNull(message = "Approval type is required")
    private ApprovalType approvalType;

    private String vehicleNumber;

    @Size(max = 500)
    private String purpose;

    @NotNull(message = "Expected arrival is required")
    private LocalDateTime expectedArrival;

    @NotNull(message = "Valid until time is required")
    private LocalDateTime validUntil;

    public CreateVisitorRequest() {
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
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
}