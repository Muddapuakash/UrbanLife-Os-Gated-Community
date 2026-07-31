package com.urbanlife.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class GateCheckInRequest {

    @NotBlank(message = "Pass code is required")
    private String passCode;

    @NotNull(message = "Security user id is required")
    private Long securityUserId;

    public GateCheckInRequest() {
    }

    public String getPassCode() {
        return passCode;
    }

    public void setPassCode(String passCode) {
        this.passCode = passCode;
    }

    public Long getSecurityUserId() {
        return securityUserId;
    }

    public void setSecurityUserId(Long securityUserId) {
        this.securityUserId = securityUserId;
    }
}