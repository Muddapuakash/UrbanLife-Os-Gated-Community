package com.urbanlife.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.urbanlife.dto.AssignComplaintRequest;
import com.urbanlife.dto.ComplaintResponse;
import com.urbanlife.dto.CreateComplaintRequest;
import com.urbanlife.dto.UpdateComplaintRequest;
import com.urbanlife.dto.UpdateComplaintStatusRequest;
import com.urbanlife.entity.Complaint;
import com.urbanlife.entity.Resident;
import com.urbanlife.entity.User;
import com.urbanlife.enums.ComplaintCategory;
import com.urbanlife.enums.ComplaintPriority;
import com.urbanlife.enums.ComplaintStatus;
import com.urbanlife.enums.ResidentStatus;
import com.urbanlife.enums.RoleName;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.ComplaintRepository;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.repository.ResidentRepository;
import com.urbanlife.repository.UserRepository;
import com.urbanlife.service.ComplaintService;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ResidentRepository residentRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;

    public ComplaintServiceImpl(
            ComplaintRepository complaintRepository,
            ResidentRepository residentRepository,
            UserRepository userRepository,
            CommunityRepository communityRepository) {

        this.complaintRepository = complaintRepository;
        this.residentRepository = residentRepository;
        this.userRepository = userRepository;
        this.communityRepository = communityRepository;
    }

    @Override
    public ComplaintResponse createComplaint(
            CreateComplaintRequest request) {

        Resident resident =
                residentRepository.findById(request.getResidentId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Resident not found with id: "
                                    + request.getResidentId()));

        if (resident.getStatus() != ResidentStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Only active residents can raise complaints");
        }

        Complaint complaint = new Complaint();

        complaint.setResident(resident);
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setCategory(request.getCategory());
        complaint.setPriority(request.getPriority());
        complaint.setStatus(ComplaintStatus.OPEN);

        Complaint savedComplaint =
                complaintRepository.save(complaint);

        return mapToResponse(savedComplaint);
    }

    @Override
    public ComplaintResponse getComplaintById(
            Long complaintId) {

        return mapToResponse(findComplaint(complaintId));
    }

    @Override
    public List<ComplaintResponse> getAllComplaints() {

        return mapList(complaintRepository.findAll());
    }

    @Override
    public List<ComplaintResponse> getComplaintsByResident(
            Long residentId) {

        if (!residentRepository.existsById(residentId)) {
            throw new ResourceNotFoundException(
                    "Resident not found with id: "
                            + residentId);
        }

        return mapList(
                complaintRepository
                    .findByResidentResidentId(residentId));
    }

    @Override
    public List<ComplaintResponse> getComplaintsByCommunity(
            Long communityId) {

        if (!communityRepository.existsById(communityId)) {
            throw new ResourceNotFoundException(
                    "Community not found with id: "
                            + communityId);
        }

        return mapList(
                complaintRepository
                    .findByResidentFlatBlockCommunityCommunityId(
                            communityId));
    }

    @Override
    public List<ComplaintResponse> getComplaintsByAssignedUser(
            Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId);
        }

        return mapList(
                complaintRepository
                    .findByAssignedToUserId(userId));
    }

    @Override
    public List<ComplaintResponse> getComplaintsByStatus(
            ComplaintStatus status) {

        return mapList(
                complaintRepository.findByStatus(status));
    }

    @Override
    public List<ComplaintResponse> getComplaintsByPriority(
            ComplaintPriority priority) {

        return mapList(
                complaintRepository.findByPriority(priority));
    }

    @Override
    public List<ComplaintResponse> getComplaintsByCategory(
            ComplaintCategory category) {

        return mapList(
                complaintRepository.findByCategory(category));
    }

    @Override
    public ComplaintResponse updateComplaint(
            Long complaintId,
            UpdateComplaintRequest request) {

        Complaint complaint = findComplaint(complaintId);

        if (complaint.getStatus() != ComplaintStatus.OPEN) {
            throw new IllegalArgumentException(
                    "Only OPEN complaints can be edited");
        }

        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setCategory(request.getCategory());
        complaint.setPriority(request.getPriority());

        return mapToResponse(
                complaintRepository.save(complaint));
    }

    @Override
    public ComplaintResponse assignComplaint(
            Long complaintId,
            AssignComplaintRequest request) {

        Complaint complaint = findComplaint(complaintId);

        User user =
                userRepository.findById(request.getUserId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "User not found with id: "
                                    + request.getUserId()));

        RoleName roleName =
                user.getRole().getRoleName();

        if (roleName != RoleName.STAFF
                && roleName != RoleName.ADMIN) {

            throw new IllegalArgumentException(
                    "Complaint can only be assigned to STAFF or ADMIN");
        }

        if (complaint.getStatus() == ComplaintStatus.CLOSED
                || complaint.getStatus() == ComplaintStatus.REJECTED) {

            throw new IllegalArgumentException(
                    "Closed or rejected complaint cannot be assigned");
        }

        complaint.setAssignedTo(user);
        complaint.setAssignedAt(LocalDateTime.now());
        complaint.setStatus(ComplaintStatus.ASSIGNED);

        return mapToResponse(
                complaintRepository.save(complaint));
    }

    @Override
    public ComplaintResponse updateComplaintStatus(
            Long complaintId,
            UpdateComplaintStatusRequest request) {

        Complaint complaint = findComplaint(complaintId);

        ComplaintStatus newStatus = request.getStatus();

        validateStatusTransition(
                complaint.getStatus(),
                newStatus);

        if (newStatus == ComplaintStatus.IN_PROGRESS
                && complaint.getAssignedTo() == null) {

            throw new IllegalArgumentException(
                    "Complaint must be assigned before starting work");
        }

        if (newStatus == ComplaintStatus.RESOLVED) {

            if (request.getResolutionNote() == null
                    || request.getResolutionNote().isBlank()) {

                throw new IllegalArgumentException(
                        "Resolution note is required when resolving complaint");
            }

            complaint.setResolutionNote(
                    request.getResolutionNote());

            complaint.setResolvedAt(
                    LocalDateTime.now());
        }

        if (newStatus == ComplaintStatus.CLOSED) {

            if (complaint.getResolvedAt() == null) {
                throw new IllegalArgumentException(
                        "Complaint must be resolved before it can be closed");
            }

            complaint.setClosedAt(
                    LocalDateTime.now());
        }

        complaint.setStatus(newStatus);

        return mapToResponse(
                complaintRepository.save(complaint));
    }

    @Override
    public void deleteComplaint(Long complaintId) {

        Complaint complaint = findComplaint(complaintId);

        if (complaint.getStatus() != ComplaintStatus.OPEN) {
            throw new IllegalArgumentException(
                    "Only OPEN complaints can be deleted");
        }

        complaintRepository.delete(complaint);
    }

    private void validateStatusTransition(
            ComplaintStatus current,
            ComplaintStatus next) {

        if (current == next) {
            return;
        }

        boolean valid = switch (current) {

            case OPEN ->
                next == ComplaintStatus.ASSIGNED
                || next == ComplaintStatus.REJECTED;

            case ASSIGNED ->
                next == ComplaintStatus.IN_PROGRESS
                || next == ComplaintStatus.REJECTED;

            case IN_PROGRESS ->
                next == ComplaintStatus.RESOLVED;

            case RESOLVED ->
                next == ComplaintStatus.CLOSED
                || next == ComplaintStatus.IN_PROGRESS;

            case CLOSED, REJECTED -> false;
        };

        if (!valid) {
            throw new IllegalArgumentException(
                    "Invalid complaint status transition: "
                            + current + " -> " + next);
        }
    }

    private Complaint findComplaint(Long complaintId) {

        return complaintRepository.findById(complaintId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Complaint not found with id: "
                                + complaintId));
    }

    private List<ComplaintResponse> mapList(
            List<Complaint> complaints) {

        return complaints.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ComplaintResponse mapToResponse(
            Complaint complaint) {

        ComplaintResponse response =
                new ComplaintResponse();

        response.setComplaintId(
                complaint.getComplaintId());

        response.setTitle(complaint.getTitle());
        response.setDescription(
                complaint.getDescription());

        response.setCategory(
                complaint.getCategory());

        response.setPriority(
                complaint.getPriority());

        response.setStatus(
                complaint.getStatus());

        Resident resident = complaint.getResident();

        response.setResidentId(
                resident.getResidentId());

        response.setResidentName(
                resident.getUser().getFirstName()
                + " "
                + resident.getUser().getLastName());

        response.setFlatId(
                resident.getFlat().getFlatId());

        response.setFlatNumber(
                resident.getFlat().getFlatNumber());

        response.setBlockId(
                resident.getFlat()
                    .getBlock()
                    .getBlockId());

        response.setBlockName(
                resident.getFlat()
                    .getBlock()
                    .getBlockName());

        response.setCommunityId(
                resident.getFlat()
                    .getBlock()
                    .getCommunity()
                    .getCommunityId());

        response.setCommunityName(
                resident.getFlat()
                    .getBlock()
                    .getCommunity()
                    .getName());

        if (complaint.getAssignedTo() != null) {

            User assigned = complaint.getAssignedTo();

            response.setAssignedToUserId(
                    assigned.getUserId());

            response.setAssignedToName(
                    assigned.getFirstName()
                    + " "
                    + assigned.getLastName());
        }

        response.setResolutionNote(
                complaint.getResolutionNote());

        response.setAssignedAt(
                complaint.getAssignedAt());

        response.setResolvedAt(
                complaint.getResolvedAt());

        response.setClosedAt(
                complaint.getClosedAt());

        response.setCreatedAt(
                complaint.getCreatedAt());

        response.setUpdatedAt(
                complaint.getUpdatedAt());

        return response;
    }
}