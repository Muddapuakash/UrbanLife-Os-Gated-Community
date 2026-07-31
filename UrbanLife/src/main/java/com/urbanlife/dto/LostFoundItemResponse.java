package com.urbanlife.dto;

import java.time.LocalDateTime;

import com.urbanlife.enums.ItemCategory;
import com.urbanlife.enums.ItemReportType;
import com.urbanlife.enums.LostFoundStatus;

public class LostFoundItemResponse {

    private Long itemId;

    private String itemName;
    private String description;

    private ItemCategory category;
    private ItemReportType reportType;

    private String location;

    private LocalDateTime incidentTime;

    private LostFoundStatus status;

    private Long reportedByUserId;
    private Long communityId;

    private String imageUrl;

    private LocalDateTime createdAt;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

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

    public LostFoundStatus getStatus() {
        return status;
    }

    public void setStatus(
            LostFoundStatus status) {
        this.status = status;
    }

    public Long getReportedByUserId() {
        return reportedByUserId;
    }

    public void setReportedByUserId(
            Long reportedByUserId) {
        this.reportedByUserId =
                reportedByUserId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}