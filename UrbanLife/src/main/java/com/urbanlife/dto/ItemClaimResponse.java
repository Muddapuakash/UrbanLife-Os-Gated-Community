package com.urbanlife.dto;

import java.time.LocalDateTime;

import com.urbanlife.enums.ClaimStatus;

public class ItemClaimResponse {

    private Long claimId;

    private Long itemId;
    private String itemName;

    private Long userId;

    private String proofDescription;

    private ClaimStatus status;

    private LocalDateTime claimedAt;
    private LocalDateTime reviewedAt;

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
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

    public void setItemName(
            String itemName) {
        this.itemName = itemName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getProofDescription() {
        return proofDescription;
    }

    public void setProofDescription(
            String proofDescription) {
        this.proofDescription =
                proofDescription;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(
            ClaimStatus status) {
        this.status = status;
    }

    public LocalDateTime getClaimedAt() {
        return claimedAt;
    }

    public void setClaimedAt(
            LocalDateTime claimedAt) {
        this.claimedAt = claimedAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(
            LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}