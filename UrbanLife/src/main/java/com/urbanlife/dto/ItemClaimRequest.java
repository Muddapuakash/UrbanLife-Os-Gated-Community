package com.urbanlife.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ItemClaimRequest {

    @NotBlank
    @Size(max = 1000)
    private String proofDescription;

    public String getProofDescription() {
        return proofDescription;
    }

    public void setProofDescription(
            String proofDescription) {
        this.proofDescription =
                proofDescription;
    }
}