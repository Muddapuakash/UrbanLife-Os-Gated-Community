package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.ParkingSlot;
import com.urbanlife.enums.ParkingSlotStatus;
import com.urbanlife.enums.ParkingSlotType;

public interface ParkingSlotRepository
        extends JpaRepository<ParkingSlot, Long> {

    boolean existsByCommunityCommunityIdAndSlotNumber(
            Long communityId,
            String slotNumber);

    List<ParkingSlot> findByCommunityCommunityId(
            Long communityId);

    List<ParkingSlot>
        findByCommunityCommunityIdAndStatus(
            Long communityId,
            ParkingSlotStatus status);

    List<ParkingSlot>
        findByCommunityCommunityIdAndSlotType(
            Long communityId,
            ParkingSlotType slotType);
}