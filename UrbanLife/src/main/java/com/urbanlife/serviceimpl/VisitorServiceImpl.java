package com.urbanlife.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.urbanlife.dto.CreateVisitorRequest;
import com.urbanlife.dto.GateCheckInRequest;
import com.urbanlife.dto.GateCheckOutRequest;
import com.urbanlife.dto.VisitorApprovalRequest;
import com.urbanlife.dto.VisitorResponse;
import com.urbanlife.entity.Resident;
import com.urbanlife.entity.User;
import com.urbanlife.entity.Visitor;
import com.urbanlife.enums.ApprovalType;
import com.urbanlife.enums.ResidentStatus;
import com.urbanlife.enums.RoleName;
import com.urbanlife.enums.VisitStatus;
import com.urbanlife.enums.VisitorType;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.repository.FlatRepository;
import com.urbanlife.repository.ResidentRepository;
import com.urbanlife.repository.UserRepository;
import com.urbanlife.repository.VisitorRepository;
import com.urbanlife.service.VisitorService;

@Service
public class VisitorServiceImpl implements VisitorService {

    private final VisitorRepository visitorRepository;
    private final ResidentRepository residentRepository;
    private final UserRepository userRepository;
    private final FlatRepository flatRepository;
    private final CommunityRepository communityRepository;

    public VisitorServiceImpl(
            VisitorRepository visitorRepository,
            ResidentRepository residentRepository,
            UserRepository userRepository,
            FlatRepository flatRepository,
            CommunityRepository communityRepository) {

        this.visitorRepository = visitorRepository;
        this.residentRepository = residentRepository;
        this.userRepository = userRepository;
        this.flatRepository = flatRepository;
        this.communityRepository = communityRepository;
    }

    @Override
    public VisitorResponse createVisitor(
            CreateVisitorRequest request) {

        Resident resident =
                residentRepository.findById(request.getResidentId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Resident not found with id: "
                                    + request.getResidentId()));

        if (resident.getStatus() != ResidentStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Only active residents can create visitor requests");
        }

        if (request.getValidUntil()
                .isBefore(request.getExpectedArrival())) {

            throw new IllegalArgumentException(
                    "Valid-until time cannot be before expected arrival");
        }

        Visitor visitor = new Visitor();

        visitor.setVisitorName(request.getVisitorName());
        visitor.setPhone(request.getPhone());
        visitor.setVisitorType(request.getVisitorType());
        visitor.setApprovalType(request.getApprovalType());
        visitor.setVehicleNumber(request.getVehicleNumber());
        visitor.setPurpose(request.getPurpose());

        visitor.setResident(resident);

        visitor.setExpectedArrival(
                request.getExpectedArrival());

        visitor.setValidUntil(
                request.getValidUntil());

        visitor.setPassCode(generatePassCode());

        /*
         * If resident creates a PRE_APPROVED visit,
         * it is automatically approved.
         *
         * GATE_APPROVAL waits for resident approval.
         */
        if (request.getApprovalType()
                == ApprovalType.PRE_APPROVED) {

            visitor.setStatus(VisitStatus.APPROVED);

        } else {

            visitor.setStatus(VisitStatus.PENDING);
        }

        return mapToResponse(
                visitorRepository.save(visitor));
    }

    @Override
    public VisitorResponse getVisitorById(Long visitorId) {

        return mapToResponse(findVisitor(visitorId));
    }

    @Override
    public VisitorResponse getVisitorByPassCode(
            String passCode) {

        Visitor visitor =
                visitorRepository.findByPassCode(passCode)
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Visitor not found for pass code: "
                                    + passCode));

        return mapToResponse(visitor);
    }

    @Override
    public List<VisitorResponse> getAllVisitors() {

        return mapList(visitorRepository.findAll());
    }

    @Override
    public List<VisitorResponse> getVisitorsByResident(
            Long residentId) {

        if (!residentRepository.existsById(residentId)) {
            throw new ResourceNotFoundException(
                    "Resident not found with id: "
                            + residentId);
        }

        return mapList(
                visitorRepository
                    .findByResidentResidentId(residentId));
    }

    @Override
    public List<VisitorResponse> getVisitorsByFlat(
            Long flatId) {

        if (!flatRepository.existsById(flatId)) {
            throw new ResourceNotFoundException(
                    "Flat not found with id: " + flatId);
        }

        return mapList(
                visitorRepository
                    .findByResidentFlatFlatId(flatId));
    }

    @Override
    public List<VisitorResponse> getVisitorsByCommunity(
            Long communityId) {

        if (!communityRepository.existsById(communityId)) {
            throw new ResourceNotFoundException(
                    "Community not found with id: "
                            + communityId);
        }

        return mapList(
                visitorRepository
                    .findByResidentFlatBlockCommunityCommunityId(
                            communityId));
    }

    @Override
    public List<VisitorResponse> getVisitorsByStatus(
            VisitStatus status) {

        return mapList(
                visitorRepository.findByStatus(status));
    }

    @Override
    public List<VisitorResponse> getVisitorsByType(
            VisitorType visitorType) {

        return mapList(
                visitorRepository
                    .findByVisitorType(visitorType));
    }

    @Override
    public VisitorResponse approveOrRejectVisitor(
            Long visitorId,
            VisitorApprovalRequest request) {

        Visitor visitor = findVisitor(visitorId);

        if (visitor.getStatus() != VisitStatus.PENDING) {

            throw new IllegalArgumentException(
                    "Only PENDING visitor requests can be approved or rejected");
        }

        if (Boolean.TRUE.equals(request.getApproved())) {

            visitor.setStatus(VisitStatus.APPROVED);

        } else {

            visitor.setStatus(VisitStatus.REJECTED);
        }

        return mapToResponse(
                visitorRepository.save(visitor));
    }

    @Override
    public VisitorResponse checkIn(
            GateCheckInRequest request) {

        Visitor visitor =
                visitorRepository
                    .findByPassCode(request.getPassCode())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Invalid visitor pass code"));

        if (visitor.getStatus() != VisitStatus.APPROVED) {

            throw new IllegalArgumentException(
                    "Only APPROVED visitors can check in");
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(visitor.getValidUntil())) {

            visitor.setStatus(VisitStatus.EXPIRED);
            visitorRepository.save(visitor);

            throw new IllegalArgumentException(
                    "Visitor pass has expired");
        }

        User securityUser =
                findSecurityUser(
                        request.getSecurityUserId());

        visitor.setStatus(VisitStatus.CHECKED_IN);
        visitor.setCheckInTime(now);
        visitor.setCheckedInBy(securityUser);

        return mapToResponse(
                visitorRepository.save(visitor));
    }

    @Override
    public VisitorResponse checkOut(
            Long visitorId,
            GateCheckOutRequest request) {

        Visitor visitor = findVisitor(visitorId);

        if (visitor.getStatus()
                != VisitStatus.CHECKED_IN) {

            throw new IllegalArgumentException(
                    "Visitor must be CHECKED_IN before checkout");
        }

        User securityUser =
                findSecurityUser(
                        request.getSecurityUserId());

        visitor.setStatus(VisitStatus.CHECKED_OUT);

        visitor.setCheckOutTime(
                LocalDateTime.now());

        visitor.setCheckedOutBy(securityUser);

        return mapToResponse(
                visitorRepository.save(visitor));
    }

    @Override
    public VisitorResponse cancelVisitor(Long visitorId) {

        Visitor visitor = findVisitor(visitorId);

        if (visitor.getStatus() != VisitStatus.PENDING
                && visitor.getStatus()
                    != VisitStatus.APPROVED) {

            throw new IllegalArgumentException(
                    "Only PENDING or APPROVED visits can be cancelled");
        }

        visitor.setStatus(VisitStatus.CANCELLED);

        return mapToResponse(
                visitorRepository.save(visitor));
    }

    @Override
    public void deleteVisitor(Long visitorId) {

        Visitor visitor = findVisitor(visitorId);

        if (visitor.getStatus()
                == VisitStatus.CHECKED_IN) {

            throw new IllegalArgumentException(
                    "Checked-in visitor cannot be deleted");
        }

        visitorRepository.delete(visitor);
    }

    private User findSecurityUser(Long userId) {

        User user =
                userRepository.findById(userId)
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Security user not found with id: "
                                    + userId));

        RoleName role =
                user.getRole().getRoleName();

        if (role != RoleName.SECURITY
                && role != RoleName.STAFF
                && role != RoleName.ADMIN
                && role != RoleName.SUPER_ADMIN) {

            throw new IllegalArgumentException(
                    "Gate operation can only be performed by SECURITY, STAFF or ADMIN");
        }

        return user;
    }

    private Visitor findVisitor(Long visitorId) {

        return visitorRepository.findById(visitorId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Visitor not found with id: "
                                + visitorId));
    }

    private String generatePassCode() {

        String passCode;

        do {

            passCode =
                    "VIS-"
                    + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

        } while (visitorRepository
                .existsByPassCode(passCode));

        return passCode;
    }

    private List<VisitorResponse> mapList(
            List<Visitor> visitors) {

        return visitors.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private VisitorResponse mapToResponse(
            Visitor visitor) {

        VisitorResponse response =
                new VisitorResponse();

        response.setVisitorId(visitor.getVisitorId());

        response.setVisitorName(
                visitor.getVisitorName());

        response.setPhone(visitor.getPhone());

        response.setVisitorType(
                visitor.getVisitorType());

        response.setApprovalType(
                visitor.getApprovalType());

        response.setStatus(visitor.getStatus());

        response.setVehicleNumber(
                visitor.getVehicleNumber());

        response.setPurpose(visitor.getPurpose());

        response.setPassCode(visitor.getPassCode());

        Resident resident = visitor.getResident();

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

        response.setExpectedArrival(
                visitor.getExpectedArrival());

        response.setValidUntil(
                visitor.getValidUntil());

        response.setCheckInTime(
                visitor.getCheckInTime());

        response.setCheckOutTime(
                visitor.getCheckOutTime());

        if (visitor.getCheckedInBy() != null) {

            User user = visitor.getCheckedInBy();

            response.setCheckedInByUserId(
                    user.getUserId());

            response.setCheckedInByName(
                    user.getFirstName()
                    + " "
                    + user.getLastName());
        }

        if (visitor.getCheckedOutBy() != null) {

            User user = visitor.getCheckedOutBy();

            response.setCheckedOutByUserId(
                    user.getUserId());

            response.setCheckedOutByName(
                    user.getFirstName()
                    + " "
                    + user.getLastName());
        }

        response.setCreatedAt(visitor.getCreatedAt());
        response.setUpdatedAt(visitor.getUpdatedAt());

        return response;
    }
}