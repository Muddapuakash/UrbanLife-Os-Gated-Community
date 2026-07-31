package com.urbanlife.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateBlockRequest {

    @NotBlank(message = "Block name is required")
    @Size(max = 100)
    private String blockName;

    @Size(max = 20)
    private String blockCode;

    @NotNull(message = "Total floors is required")
    @Min(value = 1, message = "Block must have at least one floor")
    private Integer totalFloors;

    @NotNull(message = "Community id is required")
    private Long communityId;

    public CreateBlockRequest() {
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(String blockCode) {
        this.blockCode = blockCode;
    }

    public Integer getTotalFloors() {
        return totalFloors;
    }

    public void setTotalFloors(Integer totalFloors) {
        this.totalFloors = totalFloors;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }
}