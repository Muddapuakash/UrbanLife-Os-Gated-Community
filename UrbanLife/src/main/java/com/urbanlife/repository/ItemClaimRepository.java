package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.ItemClaim;
import com.urbanlife.enums.ClaimStatus;

public interface ItemClaimRepository
        extends JpaRepository<ItemClaim, Long> {

    boolean existsByItemItemIdAndClaimantUserId(
            Long itemId,
            Long userId);

    List<ItemClaim>
        findByItemItemIdOrderByClaimedAtDesc(
            Long itemId);

    List<ItemClaim>
        findByClaimantUserIdOrderByClaimedAtDesc(
            Long userId);

    long countByItemItemIdAndStatus(
            Long itemId,
            ClaimStatus status);
}