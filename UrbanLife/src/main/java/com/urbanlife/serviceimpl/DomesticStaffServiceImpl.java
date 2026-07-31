package com.urbanlife.serviceimpl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urbanlife.dto.*;
import com.urbanlife.entity.*;
import com.urbanlife.enums.*;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.*;
import com.urbanlife.service.DomesticStaffService;

@Service
public class DomesticStaffServiceImpl
        implements DomesticStaffService {

    private final DomesticStaffRepository staffRepository;
    private final CommunityRepository communityRepository;
    private final ResidentRepository residentRepository;
    private final StaffFlatAssignmentRepository assignmentRepository;
    private final StaffRatingRepository ratingRepository;

    public DomesticStaffServiceImpl(
            DomesticStaffRepository staffRepository,
            CommunityRepository communityRepository,
            ResidentRepository residentRepository,
            StaffFlatAssignmentRepository assignmentRepository,
            StaffRatingRepository ratingRepository) {

        this.staffRepository = staffRepository;
        this.communityRepository = communityRepository;
        this.residentRepository = residentRepository;
        this.assignmentRepository = assignmentRepository;
        this.ratingRepository = ratingRepository;
    }

    @Override
    @Transactional
    public DomesticStaffResponse createStaff(
            CreateDomesticStaffRequest request) {

        Community community =
                communityRepository
                    .findById(request.getCommunityId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Community not found with id: "
                            + request.getCommunityId()));

        if (request.getStaffType() == StaffType.OTHER
                && (request.getCustomStaffType() == null
                || request.getCustomStaffType().isBlank())) {

            throw new IllegalArgumentException(
                "Custom staff type is required when type is OTHER");
        }

        // Check Aadhaar / verificationReference uniqueness across platform
        if (request.getVerificationReference() != null
                && !request.getVerificationReference().isBlank()
                && staffRepository.existsByVerificationReference(
                        request.getVerificationReference())) {

            throw new IllegalArgumentException(
                "A domestic staff member with this Aadhaar / verification ID ("
                + request.getVerificationReference()
                + ") is already registered in the system.");
        }

        DomesticStaff staff = new DomesticStaff();

        staff.setCommunity(community);
        staff.setName(request.getName());
        staff.setPhone(request.getPhone());
        staff.setStaffType(request.getStaffType());
        staff.setCustomStaffType(request.getCustomStaffType());
        staff.setAddress(request.getAddress());
        staff.setPhotoUrl(request.getPhotoUrl());
        staff.setVerificationReference(
                request.getVerificationReference());

        staff.setVerificationStatus(
                VerificationStatus.PENDING);

        staff.setStatus(StaffStatus.ACTIVE);

        return mapStaff(
                staffRepository.save(staff));
    }

    @Override
    public DomesticStaffResponse getStaffById(
            Long staffId) {

        return mapStaff(findStaff(staffId));
    }

    @Override
    public List<DomesticStaffResponse> getByCommunity(
            Long communityId) {

        validateCommunity(communityId);

        return mapStaffList(
            staffRepository
                .findByCommunityCommunityId(
                    communityId));
    }

    @Override
    public List<DomesticStaffResponse> getByType(
            Long communityId,
            StaffType type) {

        validateCommunity(communityId);

        return mapStaffList(
            staffRepository
                .findByCommunityCommunityIdAndStaffType(
                    communityId,
                    type));
    }

    @Override
    public List<DomesticStaffResponse> getByStatus(
            Long communityId,
            StaffStatus status) {

        validateCommunity(communityId);

        return mapStaffList(
            staffRepository
                .findByCommunityCommunityIdAndStatus(
                    communityId,
                    status));
    }

    @Override
    public List<DomesticStaffResponse>
            getByVerificationStatus(
                    Long communityId,
                    VerificationStatus status) {

        validateCommunity(communityId);

        return mapStaffList(
            staffRepository
                .findByCommunityCommunityIdAndVerificationStatus(
                    communityId,
                    status));
    }

    @Override
    @Transactional
    public DomesticStaffResponse verifyStaff(
            Long staffId,
            VerifyStaffRequest request) {

        DomesticStaff staff = findStaff(staffId);

        if (request.getVerificationStatus()
                == VerificationStatus.PENDING) {

            throw new IllegalArgumentException(
                "Verification result must be VERIFIED or REJECTED");
        }

        staff.setVerificationStatus(
                request.getVerificationStatus());

        staff.setVerificationRemarks(
                request.getRemarks());

        return mapStaff(
                staffRepository.save(staff));
    }

    @Override
    @Transactional
    public DomesticStaffResponse blockStaff(
            Long staffId,
            BlockStaffRequest request) {

        DomesticStaff staff = findStaff(staffId);

        staff.setStatus(StaffStatus.BLOCKED);
        staff.setBlockedReason(request.getReason());

        return mapStaff(
                staffRepository.save(staff));
    }

    @Override
    @Transactional
    public DomesticStaffResponse activateStaff(
            Long staffId) {

        DomesticStaff staff = findStaff(staffId);

        staff.setStatus(StaffStatus.ACTIVE);
        staff.setBlockedReason(null);

        return mapStaff(
                staffRepository.save(staff));
    }

    @Override
    @Transactional
    public StaffAssignmentResponse assignToResident(
            Long staffId,
            StaffAssignmentRequest request) {

        DomesticStaff staff = findStaff(staffId);

        if (staff.getStatus() != StaffStatus.ACTIVE) {
            throw new IllegalArgumentException(
                "Only ACTIVE staff can be assigned");
        }

        Resident resident =
                residentRepository
                    .findById(request.getResidentId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Resident not found with id: "
                            + request.getResidentId()));

        if (resident.getFlat() == null) {
            throw new IllegalArgumentException(
                "Resident is not assigned to a flat");
        }

        Flat flat = resident.getFlat();

        if (!flat.getBlock()
                .getCommunity()
                .getCommunityId()
                .equals(
                    staff.getCommunity()
                        .getCommunityId())) {

            throw new IllegalArgumentException(
                "Staff and resident must belong to the same community");
        }

        if (assignmentRepository
                .existsByStaffStaffIdAndFlatFlatIdAndActiveTrue(
                    staffId,
                    flat.getFlatId())) {

            throw new IllegalArgumentException(
                "Staff is already assigned to this flat");
        }

        StaffFlatAssignment assignment =
                new StaffFlatAssignment();

        assignment.setStaff(staff);
        assignment.setResident(resident);
        assignment.setFlat(flat);
        assignment.setStartDate(LocalDate.now());
        assignment.setActive(true);

        return mapAssignment(
                assignmentRepository.save(assignment));
    }

    @Override
    public List<StaffAssignmentResponse>
            getStaffAssignments(Long staffId) {

        findStaff(staffId);

        return assignmentRepository
                .findByStaffStaffIdAndActiveTrue(staffId)
                .stream()
                .map(this::mapAssignment)
                .toList();
    }

    @Override
    @Transactional
    public void removeAssignment(
            Long assignmentId) {

        StaffFlatAssignment assignment =
                assignmentRepository
                    .findById(assignmentId)
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Assignment not found with id: "
                            + assignmentId));

        if (!assignment.getActive()) {
            throw new IllegalArgumentException(
                "Assignment is already inactive");
        }

        assignment.setActive(false);
        assignment.setEndDate(LocalDate.now());

        assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public void deleteStaff(Long staffId) {

        DomesticStaff staff = findStaff(staffId);

        // 1. Delete all flat assignments (physically, not soft-delete)
        //    so FK constraint is satisfied before deleting staff
        List<com.urbanlife.entity.StaffFlatAssignment> assignments =
            assignmentRepository.findByStaffStaffId(staffId);
        if (!assignments.isEmpty()) {
            assignmentRepository.deleteAll(assignments);
        }

        // 2. Delete all ratings for this staff
        List<com.urbanlife.entity.StaffRating> ratings =
            ratingRepository.findByStaffStaffId(staffId);
        if (!ratings.isEmpty()) {
            ratingRepository.deleteAll(ratings);
        }

        // 3. Now safely delete the staff record
        staffRepository.delete(staff);
    }

    private DomesticStaff findStaff(Long id) {

        return staffRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Staff not found with id: " + id));
    }

    private void validateCommunity(Long id) {

        if (!communityRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                "Community not found with id: " + id);
        }
    }

    private List<DomesticStaffResponse> mapStaffList(
            List<DomesticStaff> list) {

        return list.stream()
                .map(this::mapStaff)
                .toList();
    }

    private DomesticStaffResponse mapStaff(
            DomesticStaff staff) {

        DomesticStaffResponse response =
                new DomesticStaffResponse();

        response.setStaffId(staff.getStaffId());

        response.setCommunityId(
            staff.getCommunity().getCommunityId());

        response.setCommunityName(
            staff.getCommunity().getName());

        response.setName(staff.getName());
        response.setPhone(staff.getPhone());

        response.setStaffType(
            staff.getStaffType());

        response.setCustomStaffType(
            staff.getCustomStaffType());

        response.setAddress(staff.getAddress());
        response.setPhotoUrl(staff.getPhotoUrl());
        response.setVerificationReference(
            staff.getVerificationReference());

        response.setVerificationStatus(
            staff.getVerificationStatus());

        response.setVerificationRemarks(
            staff.getVerificationRemarks());

        response.setStatus(staff.getStatus());

        response.setBlockedReason(
            staff.getBlockedReason());

        response.setCreatedAt(
            staff.getCreatedAt());

        List<StaffRating> ratings =
            ratingRepository
                .findByStaffStaffId(staff.getStaffId());

        double average =
            ratings.stream()
                .mapToInt(StaffRating::getRating)
                .average()
                .orElse(0.0);

        response.setAverageRating(average);

        return response;
    }

    private StaffAssignmentResponse mapAssignment(
            StaffFlatAssignment assignment) {

        StaffAssignmentResponse response =
            new StaffAssignmentResponse();

        response.setAssignmentId(
            assignment.getAssignmentId());

        response.setStaffId(
            assignment.getStaff().getStaffId());

        response.setStaffName(
            assignment.getStaff().getName());

        response.setResidentId(
            assignment.getResident().getResidentId());

        response.setResidentName(
            assignment.getResident()
                .getUser().getFirstName()
            + " "
            + assignment.getResident()
                .getUser().getLastName());

        response.setFlatId(
            assignment.getFlat().getFlatId());

        response.setFlatNumber(
            assignment.getFlat().getFlatNumber());

        response.setStartDate(
            assignment.getStartDate());

        response.setEndDate(
            assignment.getEndDate());

        response.setActive(
            assignment.getActive());

        return response;
    }
}