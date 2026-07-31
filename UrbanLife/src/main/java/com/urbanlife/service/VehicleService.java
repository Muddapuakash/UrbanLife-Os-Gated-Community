package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.CreateVehicleRequest;
import com.urbanlife.dto.VehicleResponse;

public interface VehicleService {

    VehicleResponse createVehicle(
            CreateVehicleRequest request);

    VehicleResponse getVehicleById(Long vehicleId);

    VehicleResponse getVehicleByNumber(
            String vehicleNumber);

    List<VehicleResponse> getAllVehicles();

    List<VehicleResponse> getVehiclesByResident(
            Long residentId);

    List<VehicleResponse> getVehiclesByCommunity(
            Long communityId);

    VehicleResponse deactivateVehicle(Long vehicleId);
}