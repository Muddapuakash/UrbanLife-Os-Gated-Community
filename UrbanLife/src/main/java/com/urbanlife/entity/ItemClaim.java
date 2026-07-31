package com.urbanlife.entity;

import java.time.LocalDateTime;

import com.urbanlife.enums.ClaimStatus;

import jakarta.persistence.*;

@Entity
@Table(
    name = "item_claims",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_item_claim_user",
            columnNames = {
                "item_id",
                "user_id"
            }
        )
    },
    indexes = {
        @Index(
            name = "idx_claim_item",
            columnList = "item_id"
        )
    }
)
public class ItemClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long claimId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "item_id",
        nullable = false
    )
    private LostFoundItem item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false
    )
    private User claimant;

    /*
     * User explains why this item belongs to them.
     */
    @Column(nullable = false, length = 1000)
    private String proofDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClaimStatus status;

    @Column(
        nullable = false,
        updatable = false
    )
    private LocalDateTime claimedAt;

    private LocalDateTime reviewedAt;

    public ItemClaim() {
    }

    @PrePersist
    public void onCreate() {

        claimedAt = LocalDateTime.now();

        if (status == null) {
            status = ClaimStatus.PENDING;
        }
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public LostFoundItem getItem() {
        return item;
    }

    public void setItem(
            LostFoundItem item) {
        this.item = item;
    }

    public User getClaimant() {
        return claimant;
    }

    public void setClaimant(
            User claimant) {
        this.claimant = claimant;
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

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(
            LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}