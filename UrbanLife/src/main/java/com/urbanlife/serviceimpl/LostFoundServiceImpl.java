package com.urbanlife.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urbanlife.dto.LostFoundItemRequest;
import com.urbanlife.dto.LostFoundItemResponse;
import com.urbanlife.entity.Community;
import com.urbanlife.entity.LostFoundItem;
import com.urbanlife.entity.User;
import com.urbanlife.enums.ItemCategory;
import com.urbanlife.enums.ItemReportType;
import com.urbanlife.enums.LostFoundStatus;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.repository.LostFoundItemRepository;
import com.urbanlife.repository.UserRepository;
import com.urbanlife.service.LostFoundService;

@Service
@Transactional
public class LostFoundServiceImpl
        implements LostFoundService {

    private final LostFoundItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;

    public LostFoundServiceImpl(
            LostFoundItemRepository itemRepository,
            UserRepository userRepository,
            CommunityRepository communityRepository) {

        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.communityRepository =
                communityRepository;
    }

    @Override
    public LostFoundItemResponse reportItem(
            LostFoundItemRequest request) {

        User user = findUser(request.getUserId());

        Community community =
                findCommunity(
                    request.getCommunityId());

        LostFoundItem item =
                new LostFoundItem();

        mapRequest(item, request);

        item.setReportedBy(user);
        item.setCommunity(community);
        item.setStatus(
                LostFoundStatus.OPEN);

        return mapToResponse(
                itemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public LostFoundItemResponse getItem(
            Long itemId) {

        return mapToResponse(
                findItem(itemId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LostFoundItemResponse>
            getCommunityItems(
                Long communityId) {

        findCommunity(communityId);

        return itemRepository
                .findByCommunityCommunityIdOrderByCreatedAtDesc(
                    communityId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LostFoundItemResponse> getOpenItems(
            Long communityId,
            ItemReportType reportType) {

        findCommunity(communityId);

        return itemRepository
                .findByCommunityCommunityIdAndReportTypeAndStatusOrderByCreatedAtDesc(
                    communityId,
                    reportType,
                    LostFoundStatus.OPEN)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LostFoundItemResponse>
            getItemsByCategory(
                Long communityId,
                ItemCategory category) {

        findCommunity(communityId);

        return itemRepository
                .findByCommunityCommunityIdAndCategoryAndStatusOrderByCreatedAtDesc(
                    communityId,
                    category,
                    LostFoundStatus.OPEN)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LostFoundItemResponse>
            getUserReports(Long userId) {

        findUser(userId);

        return itemRepository
                .findByReportedByUserIdOrderByCreatedAtDesc(
                    userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public LostFoundItemResponse updateItem(
            Long itemId,
            Long userId,
            LostFoundItemRequest request) {

        LostFoundItem item =
                findItem(itemId);

        validateOwner(item, userId);

        if (item.getStatus()
                != LostFoundStatus.OPEN) {

            throw new IllegalStateException(
                "Only open reports can be updated");
        }

        mapRequest(item, request);

        return mapToResponse(
                itemRepository.save(item));
    }

    @Override
    public LostFoundItemResponse closeItem(
            Long itemId) {

        LostFoundItem item =
                findItem(itemId);

        if (item.getStatus()
                == LostFoundStatus.CLOSED) {

            throw new IllegalStateException(
                "Item report is already closed");
        }

        item.setStatus(
                LostFoundStatus.CLOSED);

        return mapToResponse(
                itemRepository.save(item));
    }

    @Override
    public void deleteItem(
            Long itemId,
            Long userId) {

        LostFoundItem item =
                findItem(itemId);

        validateOwner(item, userId);

        if (item.getStatus()
                != LostFoundStatus.OPEN) {

            throw new IllegalStateException(
                "Only open item reports can be deleted");
        }

        itemRepository.delete(item);
    }

    private LostFoundItem findItem(
            Long itemId) {

        return itemRepository
                .findById(itemId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Lost/Found item not found with id: "
                        + itemId));
    }

    private User findUser(Long userId) {

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "User not found with id: "
                        + userId));
    }

    private Community findCommunity(
            Long communityId) {

        return communityRepository
                .findById(communityId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Community not found with id: "
                        + communityId));
    }

    private void validateOwner(
            LostFoundItem item,
            Long userId) {

        if (!item.getReportedBy()
                .getUserId()
                .equals(userId)) {

            throw new IllegalArgumentException(
                "User is not the owner of this report");
        }
    }

    private void mapRequest(
            LostFoundItem item,
            LostFoundItemRequest request) {

        item.setItemName(
                request.getItemName());

        item.setDescription(
                request.getDescription());

        item.setCategory(
                request.getCategory());

        item.setReportType(
                request.getReportType());

        item.setLocation(
                request.getLocation());

        item.setIncidentTime(
                request.getIncidentTime());

        item.setImageUrl(
                request.getImageUrl());
    }

    private LostFoundItemResponse mapToResponse(
            LostFoundItem item) {

        LostFoundItemResponse response =
                new LostFoundItemResponse();

        response.setItemId(
                item.getItemId());

        response.setItemName(
                item.getItemName());

        response.setDescription(
                item.getDescription());

        response.setCategory(
                item.getCategory());

        response.setReportType(
                item.getReportType());

        response.setLocation(
                item.getLocation());

        response.setIncidentTime(
                item.getIncidentTime());

        response.setStatus(
                item.getStatus());

        response.setReportedByUserId(
                item.getReportedBy()
                    .getUserId());

        response.setCommunityId(
                item.getCommunity()
                    .getCommunityId());

        response.setImageUrl(
                item.getImageUrl());

        response.setCreatedAt(
                item.getCreatedAt());

        return response;
    }
}