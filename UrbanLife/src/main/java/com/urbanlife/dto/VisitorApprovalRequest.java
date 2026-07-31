package com.urbanlife.dto;

import jakarta.validation.constraints.NotNull;

public class VisitorApprovalRequest {

    @NotNull(message = "Approval value is required")
    private Boolean approved;

    public VisitorApprovalRequest() {
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }
}