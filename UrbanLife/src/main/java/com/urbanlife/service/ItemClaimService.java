package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.ItemClaimRequest;
import com.urbanlife.dto.ItemClaimResponse;

public interface ItemClaimService {

    ItemClaimResponse claimItem(
            Long itemId,
            Long userId,
            ItemClaimRequest request);

    ItemClaimResponse approveClaim(
            Long claimId);

    ItemClaimResponse rejectClaim(
            Long claimId);

    ItemClaimResponse cancelClaim(
            Long claimId,
            Long userId);

    List<ItemClaimResponse>
        getItemClaims(Long itemId);

    List<ItemClaimResponse>
        getUserClaims(Long userId);

    ItemClaimResponse markItemReturned(
            Long claimId);
}