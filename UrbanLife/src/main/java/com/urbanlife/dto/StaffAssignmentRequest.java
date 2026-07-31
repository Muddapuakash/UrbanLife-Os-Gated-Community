package com.urbanlife.dto;

import jakarta.validation.constraints.NotNull;

public class StaffAssignmentRequest {

    @NotNull
    private Long residentId;

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }
}