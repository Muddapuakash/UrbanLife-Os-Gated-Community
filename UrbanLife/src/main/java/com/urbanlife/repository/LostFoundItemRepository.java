package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.LostFoundItem;
import com.urbanlife.enums.ItemCategory;
import com.urbanlife.enums.ItemReportType;
import com.urbanlife.enums.LostFoundStatus;

public interface LostFoundItemRepository
        extends JpaRepository<LostFoundItem, Long> {
	long countByCommunityCommunityId(Long communityId);
    List<LostFoundItem>
        findByCommunityCommunityIdOrderByCreatedAtDesc(
            Long communityId);

    List<LostFoundItem>
        findByCommunityCommunityIdAndReportTypeAndStatusOrderByCreatedAtDesc(
            Long communityId,
            ItemReportType reportType,
            LostFoundStatus status);

    List<LostFoundItem>
        findByCommunityCommunityIdAndCategoryAndStatusOrderByCreatedAtDesc(
            Long communityId,
            ItemCategory category,
            LostFoundStatus status);

    List<LostFoundItem>
        findByReportedByUserIdOrderByCreatedAtDesc(
            Long userId);
}