package com.urbanlife.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.ParkingAllocation;

public interface ParkingAllocationRepository
        extends JpaRepository<ParkingAllocation, Long> {

    boolean existsByParkingSlotParkingSlotIdAndActiveTrue(
            Long parkingSlotId);

    boolean existsByVehicleVehicleIdAndActiveTrue(
            Long vehicleId);

    Optional<ParkingAllocation>
        findByVehicleVehicleIdAndActiveTrue(
            Long vehicleId);

    Optional<ParkingAllocation>
        findByParkingSlotParkingSlotIdAndActiveTrue(
            Long parkingSlotId);

    List<ParkingAllocation>
        findByVehicleResidentResidentId(
            Long residentId);

    List<ParkingAllocation>
        findByParkingSlotCommunityCommunityId(
            Long communityId);

    List<ParkingAllocation> findByActiveTrue();
}