package com.urbanlife.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.urbanlife.dto.CreateFlatRequest;
import com.urbanlife.dto.FlatResponse;
import com.urbanlife.dto.UpdateFlatRequest;
import com.urbanlife.entity.Block;
import com.urbanlife.entity.Flat;
import com.urbanlife.enums.FlatStatus;
import com.urbanlife.exception.DuplicateResourceException;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.BlockRepository;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.repository.FlatRepository;
import com.urbanlife.service.FlatService;

@Service
public class FlatServiceImpl implements FlatService {

    private final FlatRepository flatRepository;
    private final BlockRepository blockRepository;
    private final CommunityRepository communityRepository;

    public FlatServiceImpl(
            FlatRepository flatRepository,
            BlockRepository blockRepository,
            CommunityRepository communityRepository) {

        this.flatRepository = flatRepository;
        this.blockRepository = blockRepository;
        this.communityRepository = communityRepository;
    }

    @Override
    public FlatResponse createFlat(CreateFlatRequest request) {

        Block block = findBlock(request.getBlockId());

        if (request.getFloorNumber() > block.getTotalFloors()) {
            throw new IllegalArgumentException(
                    "Floor number cannot exceed block's total floors: "
                            + block.getTotalFloors());
        }

        if (flatRepository
                .existsByFlatNumberAndBlockBlockId(
                        request.getFlatNumber(),
                        request.getBlockId())) {

            throw new DuplicateResourceException(
                    "Flat number "
                            + request.getFlatNumber()
                            + " already exists in block "
                            + block.getBlockName());
        }

        Flat flat = new Flat();

        flat.setFlatNumber(request.getFlatNumber());
        flat.setFloorNumber(request.getFloorNumber());
        flat.setFlatType(request.getFlatType());
        flat.setOwnershipType(request.getOwnershipType());
        flat.setStatus(FlatStatus.VACANT);
        flat.setBlock(block);

        Flat savedFlat = flatRepository.save(flat);

        return mapToResponse(savedFlat);
    }

    @Override
    public FlatResponse getFlatById(Long flatId) {

        return mapToResponse(findFlat(flatId));
    }

    @Override
    public List<FlatResponse> getAllFlats() {

        return flatRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<FlatResponse> getFlatsByBlock(Long blockId) {

        findBlock(blockId);

        return flatRepository.findByBlockBlockId(blockId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<FlatResponse> getFlatsByCommunity(
            Long communityId) {

        if (!communityRepository.existsById(communityId)) {
            throw new ResourceNotFoundException(
                    "Community not found with id: "
                            + communityId);
        }

        return flatRepository
                .findByBlockCommunityCommunityId(communityId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<FlatResponse> getFlatsByStatus(
            FlatStatus status) {

        return flatRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public FlatResponse updateFlat(
            Long flatId,
            UpdateFlatRequest request) {

        Flat flat = findFlat(flatId);

        Block block = findBlock(request.getBlockId());

        if (request.getFloorNumber() > block.getTotalFloors()) {
            throw new IllegalArgumentException(
                    "Floor number cannot exceed block's total floors: "
                            + block.getTotalFloors());
        }

        boolean locationChanged =
                !flat.getBlock().getBlockId()
                        .equals(request.getBlockId());

        boolean numberChanged =
                !flat.getFlatNumber()
                        .equals(request.getFlatNumber());

        if ((locationChanged || numberChanged)
                && flatRepository
                        .existsByFlatNumberAndBlockBlockId(
                                request.getFlatNumber(),
                                request.getBlockId())) {

            throw new DuplicateResourceException(
                    "Flat number "
                            + request.getFlatNumber()
                            + " already exists in block "
                            + block.getBlockName());
        }

        flat.setFlatNumber(request.getFlatNumber());
        flat.setFloorNumber(request.getFloorNumber());
        flat.setFlatType(request.getFlatType());
        flat.setOwnershipType(request.getOwnershipType());
        flat.setStatus(request.getStatus());
        flat.setBlock(block);

        Flat updatedFlat = flatRepository.save(flat);

        return mapToResponse(updatedFlat);
    }

    @Override
    public void deleteFlat(Long flatId) {

        Flat flat = findFlat(flatId);

        flatRepository.delete(flat);
    }

    private Flat findFlat(Long flatId) {

        return flatRepository.findById(flatId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Flat not found with id: "
                                        + flatId));
    }

    private Block findBlock(Long blockId) {

        return blockRepository.findById(blockId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Block not found with id: "
                                        + blockId));
    }

    private FlatResponse mapToResponse(Flat flat) {

        return new FlatResponse(
                flat.getFlatId(),
                flat.getFlatNumber(),
                flat.getFloorNumber(),
                flat.getFlatType(),
                flat.getOwnershipType(),
                flat.getStatus(),

                flat.getBlock().getBlockId(),
                flat.getBlock().getBlockName(),

                flat.getBlock()
                        .getCommunity()
                        .getCommunityId(),

                flat.getBlock()
                        .getCommunity()
                        .getName(),

                flat.getCreatedAt(),
                flat.getUpdatedAt()
        );
    }
}