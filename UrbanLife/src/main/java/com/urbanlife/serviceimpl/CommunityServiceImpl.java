package com.urbanlife.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.urbanlife.dto.CommunityResponse;
import com.urbanlife.dto.CreateCommunityRequest;
import com.urbanlife.dto.UpdateCommunityRequest;
import com.urbanlife.entity.Community;
import com.urbanlife.enums.CommunityStatus;
import com.urbanlife.exception.DuplicateResourceException;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.service.CommunityService;

@Service
public class CommunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository;

    public CommunityServiceImpl(
            CommunityRepository communityRepository) {

        this.communityRepository = communityRepository;
    }

    @Override
    public CommunityResponse createCommunity(
            CreateCommunityRequest request) {

        if (communityRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Community already exists with name: "
                            + request.getName());
        }

        if (request.getRegistrationNumber() != null
                && !request.getRegistrationNumber().isBlank()
                && communityRepository.existsByRegistrationNumber(
                        request.getRegistrationNumber())) {

            throw new DuplicateResourceException(
                    "Registration number already exists: "
                            + request.getRegistrationNumber());
        }

        Community community = new Community();

        community.setName(request.getName());
        community.setRegistrationNumber(
                request.getRegistrationNumber());
        community.setEmail(request.getEmail());
        community.setPhone(request.getPhone());
        community.setAddressLine(request.getAddressLine());
        community.setCity(request.getCity());
        community.setState(request.getState());
        community.setPincode(request.getPincode());
        community.setStatus(CommunityStatus.ACTIVE);

        Community savedCommunity =
                communityRepository.save(community);

        return mapToResponse(savedCommunity);
    }

    @Override
    public CommunityResponse getCommunityById(Long communityId) {

        return mapToResponse(findCommunity(communityId));
    }

    @Override
    public List<CommunityResponse> getAllCommunities() {

        return communityRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CommunityResponse updateCommunity(
            Long communityId,
            UpdateCommunityRequest request) {

        Community community = findCommunity(communityId);

        if (!community.getName().equals(request.getName())
                && communityRepository.existsByName(
                        request.getName())) {

            throw new DuplicateResourceException(
                    "Community already exists with name: "
                            + request.getName());
        }

        String newRegistrationNumber =
                request.getRegistrationNumber();

        if (newRegistrationNumber != null
                && !newRegistrationNumber.isBlank()
                && !newRegistrationNumber.equals(
                        community.getRegistrationNumber())
                && communityRepository.existsByRegistrationNumber(
                        newRegistrationNumber)) {

            throw new DuplicateResourceException(
                    "Registration number already exists: "
                            + newRegistrationNumber);
        }

        community.setName(request.getName());
        community.setRegistrationNumber(
                request.getRegistrationNumber());
        community.setEmail(request.getEmail());
        community.setPhone(request.getPhone());
        community.setAddressLine(request.getAddressLine());
        community.setCity(request.getCity());
        community.setState(request.getState());
        community.setPincode(request.getPincode());
        community.setStatus(request.getStatus());

        Community updatedCommunity =
                communityRepository.save(community);

        return mapToResponse(updatedCommunity);
    }

    @Override
    public void deleteCommunity(Long communityId) {

        Community community = findCommunity(communityId);

        communityRepository.delete(community);
    }

    @Override
    public List<CommunityResponse> getCommunitiesByCity(
            String city) {

        return communityRepository.findByCity(city)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<CommunityResponse> getCommunitiesByStatus(
            CommunityStatus status) {

        return communityRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private Community findCommunity(Long communityId) {

        return communityRepository.findById(communityId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Community not found with id: "
                                        + communityId));
    }

    private CommunityResponse mapToResponse(
            Community community) {

        return new CommunityResponse(
                community.getCommunityId(),
                community.getName(),
                community.getRegistrationNumber(),
                community.getEmail(),
                community.getPhone(),
                community.getAddressLine(),
                community.getCity(),
                community.getState(),
                community.getPincode(),
                community.getStatus(),
                community.getCreatedAt(),
                community.getUpdatedAt()
        );
    }
}