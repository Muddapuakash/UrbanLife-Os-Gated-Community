package com.urbanlife.serviceimpl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.urbanlife.dto.CreateParkingAllocationRequest;
import com.urbanlife.dto.ParkingAllocationResponse;
import com.urbanlife.entity.ParkingAllocation;
import com.urbanlife.entity.ParkingSlot;
import com.urbanlife.entity.Resident;
import com.urbanlife.entity.Vehicle;
import com.urbanlife.enums.ParkingSlotStatus;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.repository.ParkingAllocationRepository;
import com.urbanlife.repository.ParkingSlotRepository;
import com.urbanlife.repository.ResidentRepository;
import com.urbanlife.repository.VehicleRepository;
import com.urbanlife.service.ParkingAllocationService;

@Service
public class ParkingAllocationServiceImpl
        implements ParkingAllocationService {

    private final ParkingAllocationRepository allocationRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final VehicleRepository vehicleRepository;
    private final ResidentRepository residentRepository;
    private final CommunityRepository communityRepository;

    public ParkingAllocationServiceImpl(
            ParkingAllocationRepository allocationRepository,
            ParkingSlotRepository parkingSlotRepository,
            VehicleRepository vehicleRepository,
            ResidentRepository residentRepository,
            CommunityRepository communityRepository) {

        this.allocationRepository = allocationRepository;
        this.parkingSlotRepository = parkingSlotRepository;
        this.vehicleRepository = vehicleRepository;
        this.residentRepository = residentRepository;
        this.communityRepository = communityRepository;
    }

    @Override
    public ParkingAllocationResponse allocateParking(
            CreateParkingAllocationRequest request) {

        Vehicle vehicle =
                vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Vehicle not found with id: "
                            + request.getVehicleId()));

        if (!Boolean.TRUE.equals(vehicle.getActive())) {
            throw new IllegalArgumentException(
                    "Inactive vehicle cannot receive parking");
        }

        ParkingSlot slot =
                parkingSlotRepository
                    .findById(request.getParkingSlotId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Parking slot not found with id: "
                            + request.getParkingSlotId()));

        if (slot.getStatus()
                != ParkingSlotStatus.AVAILABLE) {

            throw new IllegalArgumentException(
                    "Parking slot is not available");
        }

        if (allocationRepository
                .existsByParkingSlotParkingSlotIdAndActiveTrue(
                        slot.getParkingSlotId())) {

            throw new IllegalArgumentException(
                    "Parking slot already has an active allocation");
        }

        if (allocationRepository
                .existsByVehicleVehicleIdAndActiveTrue(
                        vehicle.getVehicleId())) {

            throw new IllegalArgumentException(
                    "Vehicle already has an active parking allocation");
        }

        Long residentCommunityId =
                vehicle.getResident()
                    .getFlat()
                    .getBlock()
                    .getCommunity()
                    .getCommunityId();

        Long slotCommunityId =
                slot.getCommunity().getCommunityId();

        if (!residentCommunityId.equals(slotCommunityId)) {

            throw new IllegalArgumentException(
                    "Vehicle and parking slot must belong to the same community");
        }

        ParkingAllocation allocation =
                new ParkingAllocation();

        allocation.setVehicle(vehicle);
        allocation.setParkingSlot(slot);
        allocation.setStartDate(request.getStartDate());
        allocation.setActive(true);

        ParkingAllocation saved =
                allocationRepository.save(allocation);

        slot.setStatus(ParkingSlotStatus.OCCUPIED);
        parkingSlotRepository.save(slot);

        return mapToResponse(saved);
    }

    @Override
    public ParkingAllocationResponse getAllocationById(
            Long allocationId) {

        return mapToResponse(
                findAllocation(allocationId));
    }

    @Override
    public List<ParkingAllocationResponse>
            getActiveAllocations() {

        return allocationRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ParkingAllocationResponse> getByResident(
            Long residentId) {

        if (!residentRepository.existsById(residentId)) {

            throw new ResourceNotFoundException(
                    "Resident not found with id: "
                            + residentId);
        }

        return allocationRepository
                .findByVehicleResidentResidentId(residentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ParkingAllocationResponse> getByCommunity(
            Long communityId) {

        if (!communityRepository.existsById(communityId)) {

            throw new ResourceNotFoundException(
                    "Community not found with id: "
                            + communityId);
        }

        return allocationRepository
                .findByParkingSlotCommunityCommunityId(
                        communityId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ParkingAllocationResponse releaseParking(
            Long allocationId) {

        ParkingAllocation allocation =
                findAllocation(allocationId);

        if (!Boolean.TRUE.equals(allocation.getActive())) {

            throw new IllegalArgumentException(
                    "Parking allocation is already released");
        }

        allocation.setActive(false);
        allocation.setEndDate(LocalDate.now());

        ParkingAllocation saved =
                allocationRepository.save(allocation);

        ParkingSlot slot =
                allocation.getParkingSlot();

        slot.setStatus(ParkingSlotStatus.AVAILABLE);

        parkingSlotRepository.save(slot);

        return mapToResponse(saved);
    }

    private ParkingAllocation findAllocation(Long id) {

        return allocationRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Parking allocation not found with id: "
                                + id));
    }

    private ParkingAllocationResponse mapToResponse(
            ParkingAllocation allocation) {

        ParkingAllocationResponse response =
                new ParkingAllocationResponse();

        Vehicle vehicle = allocation.getVehicle();
        Resident resident = vehicle.getResident();
        ParkingSlot slot = allocation.getParkingSlot();

        response.setAllocationId(
                allocation.getAllocationId());

        response.setVehicleId(
                vehicle.getVehicleId());

        response.setVehicleNumber(
                vehicle.getVehicleNumber());

        response.setParkingSlotId(
                slot.getParkingSlotId());

        response.setSlotNumber(
                slot.getSlotNumber());

        response.setResidentId(
                resident.getResidentId());

        response.setResidentName(
                resident.getUser().getFirstName()
                + " "
                + resident.getUser().getLastName());

        response.setFlatId(
                resident.getFlat().getFlatId());

        response.setFlatNumber(
                resident.getFlat().getFlatNumber());

        response.setCommunityId(
                slot.getCommunity().getCommunityId());

        response.setCommunityName(
                slot.getCommunity().getName());

        response.setStartDate(
                allocation.getStartDate());

        response.setEndDate(
                allocation.getEndDate());

        response.setActive(
                allocation.getActive());

        return response;
    }
}