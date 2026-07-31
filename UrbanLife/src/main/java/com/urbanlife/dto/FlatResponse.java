package com.urbanlife.dto;

import java.time.LocalDateTime;

import com.urbanlife.enums.FlatStatus;
import com.urbanlife.enums.FlatType;
import com.urbanlife.enums.OwnershipType;

public class FlatResponse {

    private Long flatId;
    private String flatNumber;
    private Integer floorNumber;

    private FlatType flatType;
    private OwnershipType ownershipType;
    private FlatStatus status;

    private Long blockId;
    private String blockName;

    private Long communityId;
    private String communityName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FlatResponse() {
    }

    public FlatResponse(
            Long flatId,
            String flatNumber,
            Integer floorNumber,
            FlatType flatType,
            OwnershipType ownershipType,
            FlatStatus status,
            Long blockId,
            String blockName,
            Long communityId,
            String communityName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.flatId = flatId;
        this.flatNumber = flatNumber;
        this.floorNumber = floorNumber;
        this.flatType = flatType;
        this.ownershipType = ownershipType;
        this.status = status;
        this.blockId = blockId;
        this.blockName = blockName;
        this.communityId = communityId;
        this.communityName = communityName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getFlatId() {
        return flatId;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public Integer getFloorNumber() {
        return floorNumber;
    }

    public FlatType getFlatType() {
        return flatType;
    }

    public OwnershipType getOwnershipType() {
        return ownershipType;
    }

    public FlatStatus getStatus() {
        return status;
    }

    public Long getBlockId() {
        return blockId;
    }

    public String getBlockName() {
        return blockName;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}