package com.urbanlife.dto;

import java.time.LocalDateTime;

import com.urbanlife.enums.ItemCategory;
import com.urbanlife.enums.ItemReportType;

import jakarta.validation.constraints.*;

public class LostFoundItemRequest {

    @NotBlank
    @Size(max = 150)
    private String itemName;

    @NotBlank
    @Size(max = 1000)
    private String description;

    @NotNull
    private ItemCategory category;

    @NotNull
    private ItemReportType reportType;

    @NotBlank
    @Size(max = 200)
    private String location;

    @NotNull
    private LocalDateTime incidentTime;

    @NotNull
    private Long userId;

    @NotNull
    private Long communityId;

    @Size(max = 500)
    private String imageUrl;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(
            String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {
        this.description = description;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public void setCategory(
            ItemCategory category) {
        this.category = category;
    }

    public ItemReportType getReportType() {
        return reportType;
    }

    public void setReportType(
            ItemReportType reportType) {
        this.reportType = reportType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(
            String location) {
        this.location = location;
    }

    public LocalDateTime getIncidentTime() {
        return incidentTime;
    }

    public void setIncidentTime(
            LocalDateTime incidentTime) {
        this.incidentTime = incidentTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(
            Long userId) {
        this.userId = userId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(
            Long communityId) {
        this.communityId = communityId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(
            String imageUrl) {
        this.imageUrl = imageUrl;
    }
}