package com.urbanlife.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CollectParcelRequest {

    @NotBlank
    @Size(max = 150)
    private String collectedByName;

    public String getCollectedByName() {
        return collectedByName;
    }

    public void setCollectedByName(String collectedByName) {
        this.collectedByName = collectedByName;
    }
}