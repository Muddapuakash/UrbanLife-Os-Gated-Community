package com.urbanlife.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.urbanlife.dto.CreateResidentRequest;
import com.urbanlife.dto.ResidentResponse;
import com.urbanlife.dto.UpdateResidentRequest;
import com.urbanlife.entity.Flat;
import com.urbanlife.entity.Resident;
import com.urbanlife.entity.User;
import com.urbanlife.enums.FlatStatus;
import com.urbanlife.enums.ResidentStatus;
import com.urbanlife.enums.RoleName;
import com.urbanlife.exception.DuplicateResourceException;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.BlockRepository;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.repository.FlatRepository;
import com.urbanlife.repository.ResidentRepository;
import com.urbanlife.repository.UserRepository;
import com.urbanlife.service.ResidentService;

@Service
public class ResidentServiceImpl implements ResidentService {

    private final ResidentRepository residentRepository;
    private final UserRepository userRepository;
    private final FlatRepository flatRepository;
    private final BlockRepository blockRepository;
    private final CommunityRepository communityRepository;

    public ResidentServiceImpl(
            ResidentRepository residentRepository,
            UserRepository userRepository,
            FlatRepository flatRepository,
            BlockRepository blockRepository,
            CommunityRepository communityRepository) {

        this.residentRepository = residentRepository;
        this.userRepository = userRepository;
        this.flatRepository = flatRepository;
        this.blockRepository = blockRepository;
        this.communityRepository = communityRepository;
    }

    @Override
    public ResidentResponse createResident(
            CreateResidentRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: "
                                        + request.getUserId()));

        if (user.getRole().getRoleName() != RoleName.RESIDENT) {
            throw new IllegalArgumentException(
                    "Only users with RESIDENT role can have a resident profile");
        }

        if (residentRepository.existsByUserUserId(
                request.getUserId())) {

            throw new DuplicateResourceException(
                    "Resident profile already exists for user id: "
                            + request.getUserId());
        }

        Flat flat = findFlat(request.getFlatId());

        if (Boolean.TRUE.equals(request.getPrimaryResident())
                && residentRepository
                    .existsByFlatFlatIdAndPrimaryResidentTrue(
                            request.getFlatId())) {

            throw new DuplicateResourceException(
                    "Flat already has a primary resident");
        }

        Resident resident = new Resident();

        resident.setUser(user);
        resident.setFlat(flat);
        resident.setResidentType(request.getResidentType());
        resident.setPrimaryResident(request.getPrimaryResident());
        resident.setMoveInDate(request.getMoveInDate());
        resident.setStatus(ResidentStatus.ACTIVE);
        resident.setEmergencyContactName(
                request.getEmergencyContactName());
        resident.setEmergencyContactPhone(
                request.getEmergencyContactPhone());

        Resident savedResident =
                residentRepository.save(resident);

        /*
         * Once at least one resident is added,
         * the flat becomes occupied.
         */
        if (flat.getStatus() == FlatStatus.VACANT) {
            flat.setStatus(FlatStatus.OCCUPIED);
            flatRepository.save(flat);
        }

        return mapToResponse(savedResident);
    }

    @Override
    public ResidentResponse getResidentById(
            Long residentId) {

        return mapToResponse(findResident(residentId));
    }

    @Override
    public ResidentResponse getResidentByUserId(Long userId) {

        Resident resident =
                residentRepository.findByUserUserId(userId)
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Resident not found for user id: "
                                    + userId));

        return mapToResponse(resident);
    }

    @Override
    public List<ResidentResponse> getAllResidents() {

        return residentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ResidentResponse> getResidentsByFlat(
            Long flatId) {

        findFlat(flatId);

        return residentRepository.findByFlatFlatId(flatId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ResidentResponse> getResidentsByBlock(
            Long blockId) {

        if (!blockRepository.existsById(blockId)) {
            throw new ResourceNotFoundException(
                    "Block not found with id: " + blockId);
        }

        return residentRepository
                .findByFlatBlockBlockId(blockId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ResidentResponse> getResidentsByCommunity(
            Long communityId) {

        if (!communityRepository.existsById(communityId)) {
            throw new ResourceNotFoundException(
                    "Community not found with id: "
                            + communityId);
        }

        return residentRepository
                .findByFlatBlockCommunityCommunityId(
                        communityId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ResidentResponse> getResidentsByStatus(
            ResidentStatus status) {

        return residentRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ResidentResponse updateResident(
            Long residentId,
            UpdateResidentRequest request) {

        Resident resident = findResident(residentId);

        Flat newFlat = findFlat(request.getFlatId());

        boolean changingFlat =
                !resident.getFlat()
                        .getFlatId()
                        .equals(request.getFlatId());

        if (Boolean.TRUE.equals(request.getPrimaryResident())) {

            boolean wasAlreadyPrimaryInSameFlat =
                    !changingFlat
                    && Boolean.TRUE.equals(
                            resident.getPrimaryResident());

            if (!wasAlreadyPrimaryInSameFlat
                    && residentRepository
                        .existsByFlatFlatIdAndPrimaryResidentTrue(
                                request.getFlatId())) {

                throw new DuplicateResourceException(
                        "Flat already has a primary resident");
            }
        }

        if (request.getMoveOutDate() != null
                && request.getMoveOutDate()
                    .isBefore(request.getMoveInDate())) {

            throw new IllegalArgumentException(
                    "Move-out date cannot be before move-in date");
        }

        Flat oldFlat = resident.getFlat();

        resident.setFlat(newFlat);
        resident.setResidentType(request.getResidentType());
        resident.setPrimaryResident(
                request.getPrimaryResident());
        resident.setStatus(request.getStatus());
        resident.setMoveInDate(request.getMoveInDate());
        resident.setMoveOutDate(request.getMoveOutDate());
        resident.setEmergencyContactName(
                request.getEmergencyContactName());
        resident.setEmergencyContactPhone(
                request.getEmergencyContactPhone());

        Resident updatedResident =
                residentRepository.save(resident);

        if (newFlat.getStatus() == FlatStatus.VACANT
                && request.getStatus() == ResidentStatus.ACTIVE) {

            newFlat.setStatus(FlatStatus.OCCUPIED);
            flatRepository.save(newFlat);
        }

        /*
         * If the resident moved to another flat,
         * check whether the old flat still has active residents.
         */
        if (changingFlat) {
            updateFlatOccupancy(oldFlat);
        }

        return mapToResponse(updatedResident);
    }

    @Override
    public void deleteResident(Long residentId) {

        Resident resident = findResident(residentId);

        Flat flat = resident.getFlat();

        residentRepository.delete(resident);

        updateFlatOccupancy(flat);
    }

    private void updateFlatOccupancy(Flat flat) {

        boolean hasActiveResident =
                residentRepository
                    .findByFlatFlatId(flat.getFlatId())
                    .stream()
                    .anyMatch(r ->
                        r.getStatus() == ResidentStatus.ACTIVE);

        if (!hasActiveResident) {

            flat.setStatus(FlatStatus.VACANT);
            flatRepository.save(flat);
        }
    }

    private Resident findResident(Long residentId) {

        return residentRepository.findById(residentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resident not found with id: "
                                        + residentId));
    }

    private Flat findFlat(Long flatId) {

        return flatRepository.findById(flatId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Flat not found with id: "
                                        + flatId));
    }

    private ResidentResponse mapToResponse(
            Resident resident) {

        User user = resident.getUser();
        Flat flat = resident.getFlat();

        return new ResidentResponse(
                resident.getResidentId(),

                user.getUserId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                user.getPhone(),

                resident.getResidentType(),
                resident.getStatus(),
                resident.getPrimaryResident(),

                flat.getFlatId(),
                flat.getFlatNumber(),

                flat.getBlock().getBlockId(),
                flat.getBlock().getBlockName(),

                flat.getBlock()
                    .getCommunity()
                    .getCommunityId(),

                flat.getBlock()
                    .getCommunity()
                    .getName(),

                resident.getMoveInDate(),
                resident.getMoveOutDate(),

                resident.getEmergencyContactName(),
                resident.getEmergencyContactPhone(),

                resident.getCreatedAt(),
                resident.getUpdatedAt()
        );
    }
}