package com.urbanlife.dto;

import com.urbanlife.enums.ComplaintStatus;

import jakarta.validation.constraints.NotNull;

public class UpdateComplaintStatusRequest {

    @NotNull(message = "Complaint status is required")
    private ComplaintStatus status;

    private String resolutionNote;

    public UpdateComplaintStatusRequest() {
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public String getResolutionNote() {
        return resolutionNote;
    }

    public void setResolutionNote(String resolutionNote) {
        this.resolutionNote = resolutionNote;
    }
}