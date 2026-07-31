package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.Event;
import com.urbanlife.enums.EventStatus;

public interface EventRepository
        extends JpaRepository<Event, Long> {
	long countByCommunityCommunityId(Long communityId);

	long countByCommunityCommunityIdAndStatus(
	        Long communityId,
	        EventStatus status);
    List<Event>
        findByCommunityCommunityIdOrderByStartTimeAsc(
            Long communityId);

    List<Event>
        findByCommunityCommunityIdAndStatusOrderByStartTimeAsc(
            Long communityId,
            EventStatus status);
}