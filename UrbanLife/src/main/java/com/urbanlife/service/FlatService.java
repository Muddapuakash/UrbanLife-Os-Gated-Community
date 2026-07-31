package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.CreateFlatRequest;
import com.urbanlife.dto.FlatResponse;
import com.urbanlife.dto.UpdateFlatRequest;
import com.urbanlife.enums.FlatStatus;

public interface FlatService {

    FlatResponse createFlat(CreateFlatRequest request);

    FlatResponse getFlatById(Long flatId);

    List<FlatResponse> getAllFlats();

    List<FlatResponse> getFlatsByBlock(Long blockId);

    List<FlatResponse> getFlatsByCommunity(Long communityId);

    List<FlatResponse> getFlatsByStatus(FlatStatus status);

    FlatResponse updateFlat(
            Long flatId,
            UpdateFlatRequest request);

    void deleteFlat(Long flatId);
}