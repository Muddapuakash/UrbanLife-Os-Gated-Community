package com.urbanlife.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.urbanlife.dto.EmergencyUpdateRequest;
import com.urbanlife.dto.EmergencyUpdateResponse;
import com.urbanlife.entity.Emergency;
import com.urbanlife.entity.EmergencyUpdate;
import com.urbanlife.entity.User;
import com.urbanlife.enums.EmergencyStatus;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.EmergencyRepository;
import com.urbanlife.repository.EmergencyUpdateRepository;
import com.urbanlife.repository.UserRepository;
import com.urbanlife.service.EmergencyUpdateService;

@Service
public class EmergencyUpdateServiceImpl
        implements EmergencyUpdateService {

    private final EmergencyUpdateRepository updateRepository;
    private final EmergencyRepository emergencyRepository;
    private final UserRepository userRepository;

    public EmergencyUpdateServiceImpl(
            EmergencyUpdateRepository updateRepository,
            EmergencyRepository emergencyRepository,
            UserRepository userRepository) {

        this.updateRepository = updateRepository;
        this.emergencyRepository = emergencyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public EmergencyUpdateResponse addUpdate(
            Long emergencyId,
            EmergencyUpdateRequest request) {

        Emergency emergency =
                emergencyRepository
                    .findById(emergencyId)
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Emergency not found with id: "
                            + emergencyId));

        if (emergency.getStatus()
                == EmergencyStatus.RESOLVED
                || emergency.getStatus()
                == EmergencyStatus.CANCELLED) {

            throw new IllegalArgumentException(
                "Cannot add updates to closed emergency");
        }

        User user =
                userRepository
                    .findById(request.getUserId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "User not found with id: "
                            + request.getUserId()));

        EmergencyUpdate update =
                new EmergencyUpdate();

        update.setEmergency(emergency);
        update.setUpdatedBy(user);
        update.setMessage(request.getMessage());

        return mapToResponse(
                updateRepository.save(update));
    }

    @Override
    public List<EmergencyUpdateResponse> getTimeline(
            Long emergencyId) {

        if (!emergencyRepository
                .existsById(emergencyId)) {

            throw new ResourceNotFoundException(
                "Emergency not found with id: "
                + emergencyId);
        }

        return updateRepository
                .findByEmergencyEmergencyIdOrderByCreatedAtAsc(
                    emergencyId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private EmergencyUpdateResponse mapToResponse(
            EmergencyUpdate update) {

        EmergencyUpdateResponse response =
                new EmergencyUpdateResponse();

        response.setUpdateId(
                update.getUpdateId());

        response.setEmergencyId(
                update.getEmergency()
                    .getEmergencyId());

        if (update.getUpdatedBy() != null) {

            response.setUpdatedById(
                    update.getUpdatedBy()
                        .getUserId());

            response.setUpdatedByName(
                    update.getUpdatedBy()
                        .getFirstName()
                    + " "
                    + update.getUpdatedBy()
                        .getLastName());
        }

        response.setMessage(
                update.getMessage());

        response.setCreatedAt(
                update.getCreatedAt());

        return response;
    }
}