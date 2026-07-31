package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.CreateParkingAllocationRequest;
import com.urbanlife.dto.ParkingAllocationResponse;

public interface ParkingAllocationService {

    ParkingAllocationResponse allocateParking(
            CreateParkingAllocationRequest request);

    ParkingAllocationResponse getAllocationById(
            Long allocationId);

    List<ParkingAllocationResponse> getActiveAllocations();

    List<ParkingAllocationResponse> getByResident(
            Long residentId);

    List<ParkingAllocationResponse> getByCommunity(
            Long communityId);

    ParkingAllocationResponse releaseParking(
            Long allocationId);
}