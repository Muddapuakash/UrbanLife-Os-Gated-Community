package com.urbanlife.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.Resident;
import com.urbanlife.enums.ResidentStatus;

public interface ResidentRepository
        extends JpaRepository<Resident, Long> {

    boolean existsByUserUserId(Long userId);

    Optional<Resident> findByUserUserId(Long userId);

    List<Resident> findByFlatFlatId(Long flatId);

    List<Resident> findByFlatBlockBlockId(Long blockId);

    List<Resident> findByFlatBlockCommunityCommunityId(
            Long communityId);

    List<Resident>
        findByFlatBlockCommunityCommunityIdAndStatus(
            Long communityId,
            ResidentStatus status);

    List<Resident> findByStatus(
            ResidentStatus status);

    boolean existsByFlatFlatIdAndPrimaryResidentTrue(
            Long flatId);

    long countByFlatBlockCommunityCommunityId(
            Long communityId);

    long countByFlatBlockCommunityCommunityIdAndStatus(
            Long communityId,
            ResidentStatus status);
}