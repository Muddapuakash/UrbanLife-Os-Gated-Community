package com.urbanlife.dto;

import jakarta.validation.constraints.NotNull;

public class AssignComplaintRequest {

    @NotNull(message = "User id is required")
    private Long userId;

    public AssignComplaintRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}