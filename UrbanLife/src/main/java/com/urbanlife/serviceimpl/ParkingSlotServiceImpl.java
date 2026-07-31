package com.urbanlife.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.urbanlife.dto.CreateParkingSlotRequest;
import com.urbanlife.dto.ParkingSlotResponse;
import com.urbanlife.entity.Community;
import com.urbanlife.entity.ParkingSlot;
import com.urbanlife.enums.ParkingSlotStatus;
import com.urbanlife.exception.DuplicateResourceException;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.repository.ParkingSlotRepository;
import com.urbanlife.service.ParkingSlotService;

@Service
public class ParkingSlotServiceImpl
        implements ParkingSlotService {

    private final ParkingSlotRepository parkingSlotRepository;
    private final CommunityRepository communityRepository;

    public ParkingSlotServiceImpl(
            ParkingSlotRepository parkingSlotRepository,
            CommunityRepository communityRepository) {

        this.parkingSlotRepository = parkingSlotRepository;
        this.communityRepository = communityRepository;
    }

    @Override
    public ParkingSlotResponse createParkingSlot(
            CreateParkingSlotRequest request) {

        Community community =
                communityRepository
                    .findById(request.getCommunityId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Community not found with id: "
                            + request.getCommunityId()));

        if (parkingSlotRepository
                .existsByCommunityCommunityIdAndSlotNumber(
                    request.getCommunityId(),
                    request.getSlotNumber())) {

            throw new DuplicateResourceException(
                    "Parking slot already exists");
        }

        ParkingSlot slot = new ParkingSlot();

        slot.setSlotNumber(
                request.getSlotNumber());

        slot.setSlotType(
                request.getSlotType());

        slot.setLocationDescription(
                request.getLocationDescription());

        slot.setCommunity(community);

        slot.setStatus(
                ParkingSlotStatus.AVAILABLE);

        return mapToResponse(
                parkingSlotRepository.save(slot));
    }

    @Override
    public ParkingSlotResponse getParkingSlotById(Long id) {

        return mapToResponse(findSlot(id));
    }

    @Override
    public List<ParkingSlotResponse> getAllParkingSlots() {

        return parkingSlotRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ParkingSlotResponse> getSlotsByCommunity(
            Long communityId) {

        validateCommunity(communityId);

        return parkingSlotRepository
                .findByCommunityCommunityId(communityId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ParkingSlotResponse> getSlotsByStatus(
            Long communityId,
            ParkingSlotStatus status) {

        validateCommunity(communityId);

        return parkingSlotRepository
                .findByCommunityCommunityIdAndStatus(
                        communityId,
                        status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void validateCommunity(Long id) {

        if (!communityRepository.existsById(id)) {

            throw new ResourceNotFoundException(
                    "Community not found with id: " + id);
        }
    }

    private ParkingSlot findSlot(Long id) {

        return parkingSlotRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Parking slot not found with id: "
                                + id));
    }

    private ParkingSlotResponse mapToResponse(
            ParkingSlot slot) {

        ParkingSlotResponse response =
                new ParkingSlotResponse();

        response.setParkingSlotId(
                slot.getParkingSlotId());

        response.setSlotNumber(
                slot.getSlotNumber());

        response.setSlotType(
                slot.getSlotType());

        response.setStatus(
                slot.getStatus());

        response.setLocationDescription(
                slot.getLocationDescription());

        response.setCommunityId(
                slot.getCommunity().getCommunityId());

        response.setCommunityName(
                slot.getCommunity().getName());

        return response;
    }
}