package com.urbanlife.dto;

import java.time.LocalDateTime;

import com.urbanlife.enums.BlockStatus;

public class BlockResponse {

    private Long blockId;
    private String blockName;
    private String blockCode;
    private Integer totalFloors;

    private BlockStatus status;

    private Long communityId;
    private String communityName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BlockResponse() {
    }

    public BlockResponse(
            Long blockId,
            String blockName,
            String blockCode,
            Integer totalFloors,
            BlockStatus status,
            Long communityId,
            String communityName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.blockId = blockId;
        this.blockName = blockName;
        this.blockCode = blockCode;
        this.totalFloors = totalFloors;
        this.status = status;
        this.communityId = communityId;
        this.communityName = communityName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getBlockId() {
        return blockId;
    }

    public String getBlockName() {
        return blockName;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public Integer getTotalFloors() {
        return totalFloors;
    }

    public BlockStatus getStatus() {
        return status;
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