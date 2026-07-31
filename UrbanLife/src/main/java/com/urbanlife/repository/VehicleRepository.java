package com.urbanlife.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.Vehicle;

public interface VehicleRepository
        extends JpaRepository<Vehicle, Long> {
	long countByResidentFlatBlockCommunityCommunityId(Long communityId);
    boolean existsByVehicleNumber(String vehicleNumber);

    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);

    List<Vehicle> findByResidentResidentId(Long residentId);

    List<Vehicle>
        findByResidentFlatBlockCommunityCommunityId(
            Long communityId);

    List<Vehicle> findByActiveTrue();
}