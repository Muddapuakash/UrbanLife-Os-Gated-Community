package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.Complaint;
import com.urbanlife.enums.ComplaintCategory;
import com.urbanlife.enums.ComplaintPriority;
import com.urbanlife.enums.ComplaintStatus;

public interface ComplaintRepository
        extends JpaRepository<Complaint, Long> {
	long countByResidentFlatBlockCommunityCommunityId(Long communityId);

	long countByResidentFlatBlockCommunityCommunityIdAndStatus(
	        Long communityId,
	        ComplaintStatus status);
    List<Complaint> findByResidentResidentId(
            Long residentId);

    List<Complaint> findByStatus(
            ComplaintStatus status);

    List<Complaint> findByPriority(
            ComplaintPriority priority);

    List<Complaint> findByCategory(
            ComplaintCategory category);

    List<Complaint> findByAssignedToUserId(
            Long userId);

    List<Complaint>
        findByResidentFlatBlockCommunityCommunityId(
            Long communityId);
}