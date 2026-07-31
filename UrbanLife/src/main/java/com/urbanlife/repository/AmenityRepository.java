package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.Amenity;
import com.urbanlife.enums.AmenityStatus;
import com.urbanlife.enums.AmenityType;

public interface AmenityRepository
        extends JpaRepository<Amenity, Long> {

    boolean existsByCommunityCommunityIdAndNameIgnoreCase(
            Long communityId,
            String name);

    List<Amenity> findByCommunityCommunityId(
            Long communityId);

    List<Amenity>
        findByCommunityCommunityIdAndStatus(
            Long communityId,
            AmenityStatus status);

    List<Amenity>
        findByCommunityCommunityIdAndAmenityType(
            Long communityId,
            AmenityType amenityType);
}