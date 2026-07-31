package com.urbanlife.dto;

import jakarta.validation.constraints.NotNull;

public class GateCheckOutRequest {

    @NotNull(message = "Security user id is required")
    private Long securityUserId;

    public GateCheckOutRequest() {
    }

    public Long getSecurityUserId() {
        return securityUserId;
    }

    public void setSecurityUserId(Long securityUserId) {
        this.securityUserId = securityUserId;
    }
}