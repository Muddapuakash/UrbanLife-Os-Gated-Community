package com.urbanlife.dto;

import com.urbanlife.enums.VerificationStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VerifyStaffRequest {

    @NotNull
    private VerificationStatus verificationStatus;

    @Size(max = 500)
    private String remarks;

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(
            VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}