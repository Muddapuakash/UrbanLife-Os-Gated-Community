package com.urbanlife.entity;

import java.time.LocalDateTime;

import com.urbanlife.enums.ItemCategory;
import com.urbanlife.enums.ItemReportType;
import com.urbanlife.enums.LostFoundStatus;

import jakarta.persistence.*;

@Entity
@Table(
    name = "lost_found_items",
    indexes = {
        @Index(
            name = "idx_lf_community_status",
            columnList = "community_id,status"
        ),
        @Index(
            name = "idx_lf_reporter",
            columnList = "reported_by"
        )
    }
)
public class LostFoundItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(nullable = false, length = 150)
    private String itemName;

    @Column(nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ItemCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ItemReportType reportType;

    @Column(nullable = false, length = 200)
    private String location;

    /*
     * When item was lost/found.
     */
    @Column(nullable = false)
    private LocalDateTime incidentTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LostFoundStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "reported_by",
        nullable = false
    )
    private User reportedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "community_id",
        nullable = false
    )
    private Community community;

    /*
     * Optional URL.
     * Cloudinary can be added later.
     */
    @Column(length = 500)
    private String imageUrl;

    @Column(
        nullable = false,
        updatable = false
    )
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public LostFoundItem() {
    }

    @PrePersist
    public void onCreate() {

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (status == null) {
            status = LostFoundStatus.OPEN;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
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

    public User getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(
            User reportedBy) {
        this.reportedBy = reportedBy;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(
            Community community) {
        this.community = community;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}