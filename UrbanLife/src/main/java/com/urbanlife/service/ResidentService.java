package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.CreateResidentRequest;
import com.urbanlife.dto.ResidentResponse;
import com.urbanlife.dto.UpdateResidentRequest;
import com.urbanlife.enums.ResidentStatus;

public interface ResidentService {

    ResidentResponse createResident(
            CreateResidentRequest request);

    ResidentResponse getResidentById(Long residentId);

    ResidentResponse getResidentByUserId(Long userId);

    List<ResidentResponse> getAllResidents();

    List<ResidentResponse> getResidentsByFlat(Long flatId);

    List<ResidentResponse> getResidentsByBlock(Long blockId);

    List<ResidentResponse> getResidentsByCommunity(
            Long communityId);

    List<ResidentResponse> getResidentsByStatus(
            ResidentStatus status);

    ResidentResponse updateResident(
            Long residentId,
            UpdateResidentRequest request);

    void deleteResident(Long residentId);
}