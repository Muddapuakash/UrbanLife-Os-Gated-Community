package com.urbanlife.dto;

import com.urbanlife.enums.FlatStatus;
import com.urbanlife.enums.FlatType;
import com.urbanlife.enums.OwnershipType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateFlatRequest {

    @NotBlank(message = "Flat number is required")
    @Size(max = 20)
    private String flatNumber;

    @NotNull(message = "Floor number is required")
    @Min(value = 0)
    private Integer floorNumber;

    @NotNull(message = "Flat type is required")
    private FlatType flatType;

    @NotNull(message = "Ownership type is required")
    private OwnershipType ownershipType;

    @NotNull(message = "Flat status is required")
    private FlatStatus status;

    @NotNull(message = "Block id is required")
    private Long blockId;

    public UpdateFlatRequest() {
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }

    public Integer getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
    }

    public FlatType getFlatType() {
        return flatType;
    }

    public void setFlatType(FlatType flatType) {
        this.flatType = flatType;
    }

    public OwnershipType getOwnershipType() {
        return ownershipType;
    }

    public void setOwnershipType(OwnershipType ownershipType) {
        this.ownershipType = ownershipType;
    }

    public FlatStatus getStatus() {
        return status;
    }

    public void setStatus(FlatStatus status) {
        this.status = status;
    }

    public Long getBlockId() {
        return blockId;
    }

    public void setBlockId(Long blockId) {
        this.blockId = blockId;
    }
}