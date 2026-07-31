package com.urbanlife.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.urbanlife.dto.CreateVehicleRequest;
import com.urbanlife.dto.VehicleResponse;
import com.urbanlife.entity.Resident;
import com.urbanlife.entity.Vehicle;
import com.urbanlife.enums.ResidentStatus;
import com.urbanlife.exception.DuplicateResourceException;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.repository.ResidentRepository;
import com.urbanlife.repository.VehicleRepository;
import com.urbanlife.service.VehicleService;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ResidentRepository residentRepository;
    private final CommunityRepository communityRepository;

    public VehicleServiceImpl(
            VehicleRepository vehicleRepository,
            ResidentRepository residentRepository,
            CommunityRepository communityRepository) {

        this.vehicleRepository = vehicleRepository;
        this.residentRepository = residentRepository;
        this.communityRepository = communityRepository;
    }

    @Override
    public VehicleResponse createVehicle(
            CreateVehicleRequest request) {

        Resident resident =
                residentRepository.findById(request.getResidentId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Resident not found with id: "
                            + request.getResidentId()));

        if (resident.getStatus() != ResidentStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Only active residents can register vehicles");
        }

        String vehicleNumber =
                normalizeVehicleNumber(
                        request.getVehicleNumber());

        if (vehicleRepository
                .existsByVehicleNumber(vehicleNumber)) {

            throw new DuplicateResourceException(
                    "Vehicle already registered: "
                            + vehicleNumber);
        }

        Vehicle vehicle = new Vehicle();

        vehicle.setVehicleNumber(vehicleNumber);
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setColor(request.getColor());
        vehicle.setResident(resident);
        vehicle.setActive(true);

        return mapToResponse(
                vehicleRepository.save(vehicle));
    }

    @Override
    public VehicleResponse getVehicleById(Long vehicleId) {

        return mapToResponse(findVehicle(vehicleId));
    }

    @Override
    public VehicleResponse getVehicleByNumber(
            String vehicleNumber) {

        Vehicle vehicle =
                vehicleRepository
                    .findByVehicleNumber(
                        normalizeVehicleNumber(vehicleNumber))
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Vehicle not found"));

        return mapToResponse(vehicle);
    }

    @Override
    public List<VehicleResponse> getAllVehicles() {

        return vehicleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<VehicleResponse> getVehiclesByResident(
            Long residentId) {

        if (!residentRepository.existsById(residentId)) {
            throw new ResourceNotFoundException(
                    "Resident not found with id: "
                            + residentId);
        }

        return vehicleRepository
                .findByResidentResidentId(residentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<VehicleResponse> getVehiclesByCommunity(
            Long communityId) {

        if (!communityRepository.existsById(communityId)) {
            throw new ResourceNotFoundException(
                    "Community not found with id: "
                            + communityId);
        }

        return vehicleRepository
                .findByResidentFlatBlockCommunityCommunityId(
                        communityId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public VehicleResponse deactivateVehicle(Long vehicleId) {

        Vehicle vehicle = findVehicle(vehicleId);

        vehicle.setActive(false);

        return mapToResponse(
                vehicleRepository.save(vehicle));
    }

    private Vehicle findVehicle(Long vehicleId) {

        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Vehicle not found with id: "
                                + vehicleId));
    }

    private String normalizeVehicleNumber(String number) {

        return number
                .replaceAll("\\s+", "")
                .toUpperCase();
    }

    private VehicleResponse mapToResponse(Vehicle vehicle) {

        VehicleResponse response =
                new VehicleResponse();

        response.setVehicleId(vehicle.getVehicleId());
        response.setVehicleNumber(vehicle.getVehicleNumber());
        response.setVehicleType(vehicle.getVehicleType());
        response.setBrand(vehicle.getBrand());
        response.setModel(vehicle.getModel());
        response.setColor(vehicle.getColor());
        response.setActive(vehicle.getActive());

        Resident resident = vehicle.getResident();

        response.setResidentId(resident.getResidentId());

        response.setResidentName(
                resident.getUser().getFirstName()
                + " "
                + resident.getUser().getLastName());

        response.setFlatId(
                resident.getFlat().getFlatId());

        response.setFlatNumber(
                resident.getFlat().getFlatNumber());

        response.setCommunityId(
                resident.getFlat()
                    .getBlock()
                    .getCommunity()
                    .getCommunityId());

        response.setCommunityName(
                resident.getFlat()
                    .getBlock()
                    .getCommunity()
                    .getName());

        return response;
    }
}