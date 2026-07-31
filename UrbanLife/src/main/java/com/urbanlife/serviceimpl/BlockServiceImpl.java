package com.urbanlife.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.urbanlife.dto.BlockResponse;
import com.urbanlife.dto.CreateBlockRequest;
import com.urbanlife.dto.UpdateBlockRequest;
import com.urbanlife.entity.Block;
import com.urbanlife.entity.Community;
import com.urbanlife.enums.BlockStatus;
import com.urbanlife.exception.DuplicateResourceException;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.BlockRepository;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.service.BlockService;

@Service
public class BlockServiceImpl implements BlockService {

    private final BlockRepository blockRepository;
    private final CommunityRepository communityRepository;

    public BlockServiceImpl(
            BlockRepository blockRepository,
            CommunityRepository communityRepository) {

        this.blockRepository = blockRepository;
        this.communityRepository = communityRepository;
    }

    @Override
    public BlockResponse createBlock(CreateBlockRequest request) {

        Community community = findCommunity(request.getCommunityId());

        if (blockRepository
                .existsByBlockNameAndCommunityCommunityId(
                        request.getBlockName(),
                        request.getCommunityId())) {

            throw new DuplicateResourceException(
                    "Block already exists with name: "
                            + request.getBlockName()
                            + " in community id: "
                            + request.getCommunityId());
        }

        if (request.getBlockCode() != null
                && !request.getBlockCode().isBlank()
                && blockRepository
                        .existsByBlockCodeAndCommunityCommunityId(
                                request.getBlockCode(),
                                request.getCommunityId())) {

            throw new DuplicateResourceException(
                    "Block code already exists: "
                            + request.getBlockCode()
                            + " in community id: "
                            + request.getCommunityId());
        }

        Block block = new Block();

        block.setBlockName(request.getBlockName());
        block.setBlockCode(request.getBlockCode());
        block.setTotalFloors(request.getTotalFloors());
        block.setStatus(BlockStatus.ACTIVE);
        block.setCommunity(community);

        Block savedBlock = blockRepository.save(block);

        return mapToResponse(savedBlock);
    }

    @Override
    public BlockResponse getBlockById(Long blockId) {

        return mapToResponse(findBlock(blockId));
    }

    @Override
    public List<BlockResponse> getAllBlocks() {

        return blockRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<BlockResponse> getBlocksByCommunity(
            Long communityId) {

        findCommunity(communityId);

        return blockRepository
                .findByCommunityCommunityId(communityId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<BlockResponse> getBlocksByStatus(
            BlockStatus status) {

        return blockRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BlockResponse updateBlock(
            Long blockId,
            UpdateBlockRequest request) {

        Block block = findBlock(blockId);

        Community community =
                findCommunity(request.getCommunityId());

        boolean nameChanged =
                !block.getBlockName().equals(request.getBlockName())
                || !block.getCommunity()
                        .getCommunityId()
                        .equals(request.getCommunityId());

        if (nameChanged
                && blockRepository
                        .existsByBlockNameAndCommunityCommunityId(
                                request.getBlockName(),
                                request.getCommunityId())) {

            throw new DuplicateResourceException(
                    "Block already exists with name: "
                            + request.getBlockName());
        }

        String newCode = request.getBlockCode();

        boolean codeChanged =
                newCode != null
                && !newCode.isBlank()
                && (!newCode.equals(block.getBlockCode())
                    || !block.getCommunity()
                            .getCommunityId()
                            .equals(request.getCommunityId()));

        if (codeChanged
                && blockRepository
                        .existsByBlockCodeAndCommunityCommunityId(
                                newCode,
                                request.getCommunityId())) {

            throw new DuplicateResourceException(
                    "Block code already exists: " + newCode);
        }

        block.setBlockName(request.getBlockName());
        block.setBlockCode(request.getBlockCode());
        block.setTotalFloors(request.getTotalFloors());
        block.setStatus(request.getStatus());
        block.setCommunity(community);

        Block updatedBlock = blockRepository.save(block);

        return mapToResponse(updatedBlock);
    }

    @Override
    public void deleteBlock(Long blockId) {

        Block block = findBlock(blockId);

        blockRepository.delete(block);
    }

    private Block findBlock(Long blockId) {

        return blockRepository.findById(blockId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Block not found with id: "
                                        + blockId));
    }

    private Community findCommunity(Long communityId) {

        return communityRepository.findById(communityId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Community not found with id: "
                                        + communityId));
    }

    private BlockResponse mapToResponse(Block block) {

        return new BlockResponse(
                block.getBlockId(),
                block.getBlockName(),
                block.getBlockCode(),
                block.getTotalFloors(),
                block.getStatus(),
                block.getCommunity().getCommunityId(),
                block.getCommunity().getName(),
                block.getCreatedAt(),
                block.getUpdatedAt()
        );
    }
}