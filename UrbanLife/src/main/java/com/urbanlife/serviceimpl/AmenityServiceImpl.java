package com.urbanlife.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.urbanlife.dto.AmenityResponse;
import com.urbanlife.dto.CreateAmenityRequest;
import com.urbanlife.entity.Amenity;
import com.urbanlife.entity.Community;
import com.urbanlife.enums.AmenityStatus;
import com.urbanlife.enums.AmenityType;
import com.urbanlife.exception.DuplicateResourceException;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.AmenityRepository;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.service.AmenityService;

@Service
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepository;
    private final CommunityRepository communityRepository;

    public AmenityServiceImpl(
            AmenityRepository amenityRepository,
            CommunityRepository communityRepository) {

        this.amenityRepository = amenityRepository;
        this.communityRepository = communityRepository;
    }

    @Override
    public AmenityResponse createAmenity(
            CreateAmenityRequest request) {

        Community community =
                communityRepository
                    .findById(request.getCommunityId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Community not found with id: "
                            + request.getCommunityId()));

        if (!request.getOpeningTime()
                .isBefore(request.getClosingTime())) {

            throw new IllegalArgumentException(
                    "Opening time must be before closing time");
        }

        if (amenityRepository
                .existsByCommunityCommunityIdAndNameIgnoreCase(
                    request.getCommunityId(),
                    request.getName())) {

            throw new DuplicateResourceException(
                    "Amenity already exists: "
                    + request.getName());
        }

        Amenity amenity = new Amenity();

        amenity.setName(request.getName());
        amenity.setAmenityType(request.getAmenityType());
        amenity.setDescription(request.getDescription());
        amenity.setCapacity(request.getCapacity());
        amenity.setOpeningTime(request.getOpeningTime());
        amenity.setClosingTime(request.getClosingTime());
        amenity.setMaxBookingHours(
                request.getMaxBookingHours());

        amenity.setCommunity(community);
        amenity.setStatus(AmenityStatus.AVAILABLE);

        return mapToResponse(
                amenityRepository.save(amenity));
    }

    @Override
    public AmenityResponse getAmenityById(Long amenityId) {

        return mapToResponse(findAmenity(amenityId));
    }

    @Override
    public List<AmenityResponse> getAllAmenities() {

        return amenityRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AmenityResponse> getByCommunity(
            Long communityId) {

        validateCommunity(communityId);

        return amenityRepository
                .findByCommunityCommunityId(communityId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AmenityResponse> getByStatus(
            Long communityId,
            AmenityStatus status) {

        validateCommunity(communityId);

        return amenityRepository
                .findByCommunityCommunityIdAndStatus(
                    communityId,
                    status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AmenityResponse> getByType(
            Long communityId,
            AmenityType type) {

        validateCommunity(communityId);

        return amenityRepository
                .findByCommunityCommunityIdAndAmenityType(
                    communityId,
                    type)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AmenityResponse updateStatus(
            Long amenityId,
            AmenityStatus status) {

        Amenity amenity = findAmenity(amenityId);

        amenity.setStatus(status);

        return mapToResponse(
                amenityRepository.save(amenity));
    }

    private Amenity findAmenity(Long id) {

        return amenityRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Amenity not found with id: " + id));
    }

    private void validateCommunity(Long id) {

        if (!communityRepository.existsById(id)) {

            throw new ResourceNotFoundException(
                    "Community not found with id: " + id);
        }
    }

    private AmenityResponse mapToResponse(Amenity amenity) {

        AmenityResponse response = new AmenityResponse();

        response.setAmenityId(amenity.getAmenityId());
        response.setName(amenity.getName());
        response.setAmenityType(amenity.getAmenityType());
        response.setDescription(amenity.getDescription());
        response.setCapacity(amenity.getCapacity());
        response.setOpeningTime(amenity.getOpeningTime());
        response.setClosingTime(amenity.getClosingTime());

        response.setMaxBookingHours(
                amenity.getMaxBookingHours());

        response.setStatus(amenity.getStatus());

        response.setCommunityId(
                amenity.getCommunity().getCommunityId());

        response.setCommunityName(
                amenity.getCommunity().getName());

        return response;
    }
}