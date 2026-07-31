package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.DomesticStaff;
import com.urbanlife.enums.StaffStatus;
import com.urbanlife.enums.StaffType;
import com.urbanlife.enums.VerificationStatus;

public interface DomesticStaffRepository
        extends JpaRepository<DomesticStaff, Long> {

    List<DomesticStaff>
        findByCommunityCommunityId(Long communityId);

    List<DomesticStaff>
        findByCommunityCommunityIdAndStaffType(
            Long communityId,
            StaffType staffType);

    List<DomesticStaff>
        findByCommunityCommunityIdAndStatus(
            Long communityId,
            StaffStatus status);

    List<DomesticStaff>
        findByCommunityCommunityIdAndVerificationStatus(
            Long communityId,
            VerificationStatus verificationStatus);

    boolean existsByVerificationReference(
        String verificationReference);
}