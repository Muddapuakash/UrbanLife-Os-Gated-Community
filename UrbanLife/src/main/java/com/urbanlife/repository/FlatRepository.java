package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.Flat;
import com.urbanlife.enums.FlatStatus;

public interface FlatRepository
        extends JpaRepository<Flat, Long> {
	// FlatRepository
	long countByBlockCommunityCommunityId(Long communityId);
    List<Flat> findByBlockBlockId(Long blockId);

    List<Flat> findByBlockCommunityCommunityId(Long communityId);

    List<Flat> findByStatus(FlatStatus status);

    List<Flat> findByFloorNumber(Integer floorNumber);

    boolean existsByFlatNumberAndBlockBlockId(
            String flatNumber,
            Long blockId);
}