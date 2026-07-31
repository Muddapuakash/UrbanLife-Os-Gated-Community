package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.BlockResponse;
import com.urbanlife.dto.CreateBlockRequest;
import com.urbanlife.dto.UpdateBlockRequest;
import com.urbanlife.enums.BlockStatus;

public interface BlockService {

    BlockResponse createBlock(CreateBlockRequest request);

    BlockResponse getBlockById(Long blockId);

    List<BlockResponse> getAllBlocks();

    List<BlockResponse> getBlocksByCommunity(
            Long communityId);

    List<BlockResponse> getBlocksByStatus(
            BlockStatus status);

    BlockResponse updateBlock(
            Long blockId,
            UpdateBlockRequest request);

    void deleteBlock(Long blockId);
}