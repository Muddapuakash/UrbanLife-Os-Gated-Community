package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.Emergency;
import com.urbanlife.enums.EmergencyPriority;
import com.urbanlife.enums.EmergencyStatus;
import com.urbanlife.enums.EmergencyType;

public interface EmergencyRepository
        extends JpaRepository<Emergency, Long> {
	long countByCommunityCommunityId(Long communityId);

	long countByCommunityCommunityIdAndStatus(
	        Long communityId,
	        EmergencyStatus status);
    List<Emergency>
        findByResidentResidentId(Long residentId);

    List<Emergency>
        findByCommunityCommunityId(Long communityId);

    List<Emergency>
        findByCommunityCommunityIdAndStatus(
            Long communityId,
            EmergencyStatus status);

    List<Emergency>
        findByCommunityCommunityIdAndPriority(
            Long communityId,
            EmergencyPriority priority);

    List<Emergency>
        findByCommunityCommunityIdAndEmergencyType(
            Long communityId,
            EmergencyType emergencyType);

    List<Emergency>
        findByResponderUserId(Long userId);
}