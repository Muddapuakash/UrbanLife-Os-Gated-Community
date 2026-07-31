package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.EmergencyUpdateRequest;
import com.urbanlife.dto.EmergencyUpdateResponse;

public interface EmergencyUpdateService {

    EmergencyUpdateResponse addUpdate(
            Long emergencyId,
            EmergencyUpdateRequest request);

    List<EmergencyUpdateResponse> getTimeline(
            Long emergencyId);
}