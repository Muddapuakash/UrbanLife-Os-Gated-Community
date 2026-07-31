package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.Poll;
import com.urbanlife.enums.PollStatus;

public interface PollRepository
        extends JpaRepository<Poll, Long> {
	long countByCommunityCommunityId(Long communityId);

	long countByCommunityCommunityIdAndStatus(
	        Long communityId,
	        PollStatus status);
    List<Poll>
        findByCommunityCommunityIdOrderByCreatedAtDesc(
            Long communityId);

    List<Poll>
        findByCommunityCommunityIdAndStatusOrderByEndTimeAsc(
            Long communityId,
            PollStatus status);

    List<Poll>
        findByCreatedByUserIdOrderByCreatedAtDesc(
            Long userId);
}