package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.AmenityResponse;
import com.urbanlife.dto.CreateAmenityRequest;
import com.urbanlife.enums.AmenityStatus;
import com.urbanlife.enums.AmenityType;

public interface AmenityService {

    AmenityResponse createAmenity(
            CreateAmenityRequest request);

    AmenityResponse getAmenityById(Long amenityId);

    List<AmenityResponse> getAllAmenities();

    List<AmenityResponse> getByCommunity(
            Long communityId);

    List<AmenityResponse> getByStatus(
            Long communityId,
            AmenityStatus status);

    List<AmenityResponse> getByType(
            Long communityId,
            AmenityType type);

    AmenityResponse updateStatus(
            Long amenityId,
            AmenityStatus status);
}