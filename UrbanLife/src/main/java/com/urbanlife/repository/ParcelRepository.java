package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.Parcel;
import com.urbanlife.enums.ParcelStatus;
import com.urbanlife.enums.ParcelType;

public interface ParcelRepository
        extends JpaRepository<Parcel, Long> {

    List<Parcel>
        findByResidentResidentId(Long residentId);

    List<Parcel>
        findByFlatFlatId(Long flatId);

    List<Parcel>
        findByCommunityCommunityId(Long communityId);

    List<Parcel>
        findByCommunityCommunityIdAndStatus(
            Long communityId,
            ParcelStatus status);

    List<Parcel>
        findByResidentResidentIdAndStatus(
            Long residentId,
            ParcelStatus status);

    List<Parcel>
        findByCommunityCommunityIdAndParcelType(
            Long communityId,
            ParcelType parcelType);

    List<Parcel>
        findByTrackingNumber(String trackingNumber);

    long countByCommunityCommunityIdAndStatus(
            Long communityId,
            ParcelStatus status);
}