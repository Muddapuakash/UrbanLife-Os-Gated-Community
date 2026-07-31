package com.urbanlife.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urbanlife.dto.CreateEmergencyRequest;
import com.urbanlife.dto.EmergencyResponse;
import com.urbanlife.dto.ResolveEmergencyRequest;
import com.urbanlife.entity.Community;
import com.urbanlife.entity.Emergency;
import com.urbanlife.entity.Flat;
import com.urbanlife.entity.Resident;
import com.urbanlife.entity.User;
import com.urbanlife.enums.EmergencyPriority;
import com.urbanlife.enums.EmergencyStatus;
import com.urbanlife.enums.EmergencyType;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.repository.EmergencyRepository;
import com.urbanlife.repository.ResidentRepository;
import com.urbanlife.repository.UserRepository;
import com.urbanlife.service.EmergencyService;

@Service
public class EmergencyServiceImpl
        implements EmergencyService {

    private final EmergencyRepository emergencyRepository;
    private final ResidentRepository residentRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    public EmergencyServiceImpl(
            EmergencyRepository emergencyRepository,
            ResidentRepository residentRepository,
            CommunityRepository communityRepository,
            UserRepository userRepository) {

        this.emergencyRepository = emergencyRepository;
        this.residentRepository = residentRepository;
        this.communityRepository = communityRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public EmergencyResponse createEmergency(
            CreateEmergencyRequest request) {

        Resident resident =
                residentRepository
                    .findById(request.getResidentId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Resident not found with id: "
                            + request.getResidentId()));

        Flat flat = resident.getFlat();

        if (flat == null) {
            throw new IllegalArgumentException(
                "Resident is not assigned to a flat");
        }

        Community community =
                flat.getBlock().getCommunity();

        Emergency emergency =
                new Emergency();

        emergency.setResident(resident);
        emergency.setFlat(flat);
        emergency.setCommunity(community);

        emergency.setEmergencyType(
                request.getEmergencyType());

        emergency.setPriority(
                request.getPriority());

        emergency.setDescription(
                request.getDescription());

        emergency.setLocationDetails(
                request.getLocationDetails());

        emergency.setStatus(
                EmergencyStatus.OPEN);

        return mapToResponse(
                emergencyRepository.save(emergency));
    }

    @Override
    public EmergencyResponse getEmergencyById(
            Long emergencyId) {

        return mapToResponse(
                findEmergency(emergencyId));
    }

    @Override
    public List<EmergencyResponse>
            getAllEmergencies() {

        return mapList(
                emergencyRepository.findAll());
    }

    @Override
    public List<EmergencyResponse> getByResident(
            Long residentId) {

        if (!residentRepository.existsById(residentId)) {

            throw new ResourceNotFoundException(
                "Resident not found with id: "
                + residentId);
        }

        return mapList(
                emergencyRepository
                    .findByResidentResidentId(
                        residentId));
    }

    @Override
    public List<EmergencyResponse> getByCommunity(
            Long communityId) {

        validateCommunity(communityId);

        return mapList(
                emergencyRepository
                    .findByCommunityCommunityId(
                        communityId));
    }

    @Override
    public List<EmergencyResponse> getByStatus(
            Long communityId,
            EmergencyStatus status) {

        validateCommunity(communityId);

        return mapList(
                emergencyRepository
                    .findByCommunityCommunityIdAndStatus(
                        communityId,
                        status));
    }

    @Override
    public List<EmergencyResponse> getByPriority(
            Long communityId,
            EmergencyPriority priority) {

        validateCommunity(communityId);

        return mapList(
                emergencyRepository
                    .findByCommunityCommunityIdAndPriority(
                        communityId,
                        priority));
    }

    @Override
    public List<EmergencyResponse> getByType(
            Long communityId,
            EmergencyType type) {

        validateCommunity(communityId);

        return mapList(
                emergencyRepository
                    .findByCommunityCommunityIdAndEmergencyType(
                        communityId,
                        type));
    }

    @Override
    @Transactional
    public EmergencyResponse acknowledgeEmergency(
            Long emergencyId) {

        Emergency emergency =
                findEmergency(emergencyId);

        if (emergency.getStatus()
                != EmergencyStatus.OPEN) {

            throw new IllegalArgumentException(
                "Only OPEN emergencies can be acknowledged");
        }

        emergency.setStatus(
                EmergencyStatus.ACKNOWLEDGED);

        emergency.setAcknowledgedAt(
                LocalDateTime.now());

        return mapToResponse(
                emergencyRepository.save(emergency));
    }

    @Override
    @Transactional
    public EmergencyResponse assignResponder(
            Long emergencyId,
            Long userId) {

        Emergency emergency =
                findEmergency(emergencyId);

        if (emergency.getStatus()
                == EmergencyStatus.RESOLVED
                || emergency.getStatus()
                == EmergencyStatus.CANCELLED) {

            throw new IllegalArgumentException(
                "Cannot assign responder to closed emergency");
        }

        User user =
                userRepository.findById(userId)
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "User not found with id: "
                            + userId));

        emergency.setResponder(user);

        return mapToResponse(
                emergencyRepository.save(emergency));
    }

    @Override
    @Transactional
    public EmergencyResponse startResponse(
            Long emergencyId) {

        Emergency emergency =
                findEmergency(emergencyId);

        if (emergency.getStatus()
                != EmergencyStatus.ACKNOWLEDGED) {

            throw new IllegalArgumentException(
                "Emergency must be ACKNOWLEDGED before response starts");
        }

        if (emergency.getResponder() == null) {

            throw new IllegalArgumentException(
                "Assign a responder before starting response");
        }

        emergency.setStatus(
                EmergencyStatus.IN_PROGRESS);

        emergency.setResponseStartedAt(
                LocalDateTime.now());

        return mapToResponse(
                emergencyRepository.save(emergency));
    }

    @Override
    @Transactional
    public EmergencyResponse resolveEmergency(
            Long emergencyId,
            ResolveEmergencyRequest request) {

        Emergency emergency =
                findEmergency(emergencyId);

        if (emergency.getStatus()
                != EmergencyStatus.IN_PROGRESS) {

            throw new IllegalArgumentException(
                "Only IN_PROGRESS emergencies can be resolved");
        }

        emergency.setStatus(
                EmergencyStatus.RESOLVED);

        emergency.setResolvedAt(
                LocalDateTime.now());

        emergency.setResolutionNotes(
                request.getResolutionNotes());

        return mapToResponse(
                emergencyRepository.save(emergency));
    }

    @Override
    @Transactional
    public EmergencyResponse cancelEmergency(
            Long emergencyId) {

        Emergency emergency =
                findEmergency(emergencyId);

        if (emergency.getStatus()
                != EmergencyStatus.OPEN) {

            throw new IllegalArgumentException(
                "Only OPEN emergencies can be cancelled");
        }

        emergency.setStatus(
                EmergencyStatus.CANCELLED);

        return mapToResponse(
                emergencyRepository.save(emergency));
    }

    private Emergency findEmergency(Long id) {

        return emergencyRepository
                .findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Emergency not found with id: "
                        + id));
    }

    private void validateCommunity(Long communityId) {

        if (!communityRepository
                .existsById(communityId)) {

            throw new ResourceNotFoundException(
                "Community not found with id: "
                + communityId);
        }
    }

    private List<EmergencyResponse> mapList(
            List<Emergency> emergencies) {

        return emergencies.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private EmergencyResponse mapToResponse(
            Emergency emergency) {

        EmergencyResponse response =
                new EmergencyResponse();

        response.setEmergencyId(
                emergency.getEmergencyId());

        Resident resident =
                emergency.getResident();

        response.setResidentId(
                resident.getResidentId());

        response.setResidentName(
                resident.getUser().getFirstName()
                + " "
                + resident.getUser().getLastName());

        response.setFlatId(
                emergency.getFlat().getFlatId());

        response.setFlatNumber(
                emergency.getFlat().getFlatNumber());

        response.setCommunityId(
                emergency.getCommunity()
                    .getCommunityId());

        response.setCommunityName(
                emergency.getCommunity()
                    .getName());

        response.setEmergencyType(
                emergency.getEmergencyType());

        response.setPriority(
                emergency.getPriority());

        response.setStatus(
                emergency.getStatus());

        response.setDescription(
                emergency.getDescription());

        response.setLocationDetails(
                emergency.getLocationDetails());

        if (emergency.getResponder() != null) {

            User responder =
                    emergency.getResponder();

            response.setResponderId(
                    responder.getUserId());

            response.setResponderName(
                    responder.getFirstName()
                    + " "
                    + responder.getLastName());
        }

        response.setAcknowledgedAt(
                emergency.getAcknowledgedAt());

        response.setResponseStartedAt(
                emergency.getResponseStartedAt());

        response.setResolvedAt(
                emergency.getResolvedAt());

        response.setResolutionNotes(
                emergency.getResolutionNotes());

        response.setCreatedAt(
                emergency.getCreatedAt());

        return response;
    }
}