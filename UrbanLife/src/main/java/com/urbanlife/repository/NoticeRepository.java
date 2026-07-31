package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.Notice;
import com.urbanlife.enums.NoticePriority;
import com.urbanlife.enums.NoticeStatus;
import com.urbanlife.enums.NoticeType;

public interface NoticeRepository
        extends JpaRepository<Notice, Long> {
	long countByCommunityCommunityId(Long communityId);

    List<Notice>
        findByCommunityCommunityId(Long communityId);

    List<Notice>
        findByCommunityCommunityIdAndStatus(
            Long communityId,
            NoticeStatus status);

    List<Notice>
        findByBlockBlockIdAndStatus(
            Long blockId,
            NoticeStatus status);

    List<Notice>
        findByCommunityCommunityIdAndNoticeType(
            Long communityId,
            NoticeType noticeType);

    List<Notice>
        findByCommunityCommunityIdAndPriority(
            Long communityId,
            NoticePriority priority);
}