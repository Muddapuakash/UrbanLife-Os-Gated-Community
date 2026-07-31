package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.LostFoundItemRequest;
import com.urbanlife.dto.LostFoundItemResponse;
import com.urbanlife.enums.ItemCategory;
import com.urbanlife.enums.ItemReportType;

public interface LostFoundService {

    LostFoundItemResponse reportItem(
            LostFoundItemRequest request);

    LostFoundItemResponse getItem(
            Long itemId);

    List<LostFoundItemResponse>
        getCommunityItems(Long communityId);

    List<LostFoundItemResponse>
        getOpenItems(
            Long communityId,
            ItemReportType reportType);

    List<LostFoundItemResponse>
        getItemsByCategory(
            Long communityId,
            ItemCategory category);

    List<LostFoundItemResponse>
        getUserReports(Long userId);

    LostFoundItemResponse updateItem(
            Long itemId,
            Long userId,
            LostFoundItemRequest request);

    LostFoundItemResponse closeItem(
            Long itemId);

    void deleteItem(
            Long itemId,
            Long userId);
}