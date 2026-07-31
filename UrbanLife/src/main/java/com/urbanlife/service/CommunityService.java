package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.CommunityResponse;
import com.urbanlife.dto.CreateCommunityRequest;
import com.urbanlife.dto.UpdateCommunityRequest;
import com.urbanlife.enums.CommunityStatus;

public interface CommunityService {

    CommunityResponse createCommunity(
            CreateCommunityRequest request);

    CommunityResponse getCommunityById(
            Long communityId);

    List<CommunityResponse> getAllCommunities();

    CommunityResponse updateCommunity(
            Long communityId,
            UpdateCommunityRequest request);

    void deleteCommunity(Long communityId);

    List<CommunityResponse> getCommunitiesByCity(
            String city);

    List<CommunityResponse> getCommunitiesByStatus(
            CommunityStatus status);
}