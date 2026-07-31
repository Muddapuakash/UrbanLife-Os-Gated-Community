package com.urbanlife.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urbanlife.dto.ItemClaimRequest;
import com.urbanlife.dto.ItemClaimResponse;
import com.urbanlife.entity.ItemClaim;
import com.urbanlife.entity.LostFoundItem;
import com.urbanlife.entity.User;
import com.urbanlife.enums.ClaimStatus;
import com.urbanlife.enums.ItemReportType;
import com.urbanlife.enums.LostFoundStatus;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.ItemClaimRepository;
import com.urbanlife.repository.LostFoundItemRepository;
import com.urbanlife.repository.UserRepository;
import com.urbanlife.service.ItemClaimService;

@Service
@Transactional
public class ItemClaimServiceImpl
        implements ItemClaimService {

    private final ItemClaimRepository claimRepository;
    private final LostFoundItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemClaimServiceImpl(
            ItemClaimRepository claimRepository,
            LostFoundItemRepository itemRepository,
            UserRepository userRepository) {

        this.claimRepository = claimRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemClaimResponse claimItem(
            Long itemId,
            Long userId,
            ItemClaimRequest request) {

        LostFoundItem item =
                findItem(itemId);

        User user =
                findUser(userId);

        if (item.getReportType()
                != ItemReportType.FOUND) {

            throw new IllegalStateException(
                "Only found items can be claimed");
        }

        if (item.getStatus()
                != LostFoundStatus.OPEN) {

            throw new IllegalStateException(
                "Item is not available for claim");
        }

        if (item.getReportedBy()
                .getUserId()
                .equals(userId)) {

            throw new IllegalStateException(
                "Reporter cannot claim their own found item");
        }

        if (claimRepository
                .existsByItemItemIdAndClaimantUserId(
                    itemId,
                    userId)) {

            throw new IllegalStateException(
                "User has already submitted a claim");
        }

        ItemClaim claim =
                new ItemClaim();

        claim.setItem(item);
        claim.setClaimant(user);
        claim.setProofDescription(
                request.getProofDescription());

        claim.setStatus(
                ClaimStatus.PENDING);

        return mapToResponse(
                claimRepository.save(claim));
    }

    @Override
    public ItemClaimResponse approveClaim(
            Long claimId) {

        ItemClaim claim =
                findClaim(claimId);

        if (claim.getStatus()
                != ClaimStatus.PENDING) {

            throw new IllegalStateException(
                "Only pending claims can be approved");
        }

        LostFoundItem item =
                claim.getItem();

        if (item.getStatus()
                != LostFoundStatus.OPEN) {

            throw new IllegalStateException(
                "Item is no longer available");
        }

        claim.setStatus(
                ClaimStatus.APPROVED);

        claim.setReviewedAt(
                LocalDateTime.now());

        item.setStatus(
                LostFoundStatus.CLAIMED);

        itemRepository.save(item);

        return mapToResponse(
                claimRepository.save(claim));
    }

    @Override
    public ItemClaimResponse rejectClaim(
            Long claimId) {

        ItemClaim claim =
                findClaim(claimId);

        if (claim.getStatus()
                != ClaimStatus.PENDING) {

            throw new IllegalStateException(
                "Only pending claims can be rejected");
        }

        claim.setStatus(
                ClaimStatus.REJECTED);

        claim.setReviewedAt(
                LocalDateTime.now());

        return mapToResponse(
                claimRepository.save(claim));
    }

    @Override
    public ItemClaimResponse cancelClaim(
            Long claimId,
            Long userId) {

        ItemClaim claim =
                findClaim(claimId);

        if (!claim.getClaimant()
                .getUserId()
                .equals(userId)) {

            throw new IllegalArgumentException(
                "User does not own this claim");
        }

        if (claim.getStatus()
                != ClaimStatus.PENDING) {

            throw new IllegalStateException(
                "Only pending claims can be cancelled");
        }

        claim.setStatus(
                ClaimStatus.CANCELLED);

        return mapToResponse(
                claimRepository.save(claim));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemClaimResponse>
            getItemClaims(Long itemId) {

        findItem(itemId);

        return claimRepository
                .findByItemItemIdOrderByClaimedAtDesc(
                    itemId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemClaimResponse>
            getUserClaims(Long userId) {

        findUser(userId);

        return claimRepository
                .findByClaimantUserIdOrderByClaimedAtDesc(
                    userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ItemClaimResponse markItemReturned(
            Long claimId) {

        ItemClaim claim =
                findClaim(claimId);

        if (claim.getStatus()
                != ClaimStatus.APPROVED) {

            throw new IllegalStateException(
                "Only approved claim can be returned");
        }

        LostFoundItem item =
                claim.getItem();

        item.setStatus(
                LostFoundStatus.RETURNED);

        itemRepository.save(item);

        return mapToResponse(claim);
    }

    private ItemClaim findClaim(
            Long claimId) {

        return claimRepository
                .findById(claimId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Claim not found with id: "
                        + claimId));
    }

    private LostFoundItem findItem(
            Long itemId) {

        return itemRepository
                .findById(itemId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Item not found with id: "
                        + itemId));
    }

    private User findUser(
            Long userId) {

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "User not found with id: "
                        + userId));
    }

    private ItemClaimResponse mapToResponse(
            ItemClaim claim) {

        ItemClaimResponse response =
                new ItemClaimResponse();

        response.setClaimId(
                claim.getClaimId());

        response.setItemId(
                claim.getItem()
                    .getItemId());

        response.setItemName(
                claim.getItem()
                    .getItemName());

        response.setUserId(
                claim.getClaimant()
                    .getUserId());

        response.setProofDescription(
                claim.getProofDescription());

        response.setStatus(
                claim.getStatus());

        response.setClaimedAt(
                claim.getClaimedAt());

        response.setReviewedAt(
                claim.getReviewedAt());

        return response;
    }
}