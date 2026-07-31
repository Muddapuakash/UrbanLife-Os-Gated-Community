package com.urbanlife.dto;

import jakarta.validation.constraints.NotNull;

public class StaffEntryRequest {

    @NotNull
    private Long recordedByUserId;

    public Long getRecordedByUserId() {
        return recordedByUserId;
    }

    public void setRecordedByUserId(Long recordedByUserId) {
        this.recordedByUserId = recordedByUserId;
    }
}