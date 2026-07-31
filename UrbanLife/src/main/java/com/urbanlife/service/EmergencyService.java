package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.CreateEmergencyRequest;
import com.urbanlife.dto.EmergencyResponse;
import com.urbanlife.dto.ResolveEmergencyRequest;
import com.urbanlife.enums.EmergencyPriority;
import com.urbanlife.enums.EmergencyStatus;
import com.urbanlife.enums.EmergencyType;

public interface EmergencyService {

    EmergencyResponse createEmergency(
            CreateEmergencyRequest request);

    EmergencyResponse getEmergencyById(
            Long emergencyId);

    List<EmergencyResponse> getAllEmergencies();

    List<EmergencyResponse> getByResident(
            Long residentId);

    List<EmergencyResponse> getByCommunity(
            Long communityId);

    List<EmergencyResponse> getByStatus(
            Long communityId,
            EmergencyStatus status);

    List<EmergencyResponse> getByPriority(
            Long communityId,
            EmergencyPriority priority);

    List<EmergencyResponse> getByType(
            Long communityId,
            EmergencyType type);

    EmergencyResponse acknowledgeEmergency(
            Long emergencyId);

    EmergencyResponse assignResponder(
            Long emergencyId,
            Long userId);

    EmergencyResponse startResponse(
            Long emergencyId);

    EmergencyResponse resolveEmergency(
            Long emergencyId,
            ResolveEmergencyRequest request);

    EmergencyResponse cancelEmergency(
            Long emergencyId);
}