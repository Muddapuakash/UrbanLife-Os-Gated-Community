package com.urbanlife.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ReturnParcelRequest {

    @NotBlank
    @Size(max = 500)
    private String returnReason;

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }
}