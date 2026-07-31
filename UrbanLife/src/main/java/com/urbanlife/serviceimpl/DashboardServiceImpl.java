package com.urbanlife.serviceimpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urbanlife.dto.DashboardResponse;
import com.urbanlife.entity.Community;
import com.urbanlife.enums.ComplaintStatus;
import com.urbanlife.enums.EmergencyStatus;
import com.urbanlife.enums.EventStatus;
import com.urbanlife.enums.PollStatus;
import com.urbanlife.enums.ResidentStatus;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.repository.ComplaintRepository;
import com.urbanlife.repository.EmergencyRepository;
import com.urbanlife.repository.EventRepository;
import com.urbanlife.repository.FlatRepository;
import com.urbanlife.repository.LostFoundItemRepository;
import com.urbanlife.repository.NoticeRepository;
import com.urbanlife.repository.PollRepository;
import com.urbanlife.repository.ResidentRepository;
import com.urbanlife.repository.VehicleRepository;
import com.urbanlife.repository.VisitorRepository;
import com.urbanlife.service.DashboardService;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl
        implements DashboardService {

    private final CommunityRepository communityRepository;

    private final ResidentRepository residentRepository;

    private final FlatRepository flatRepository;

    private final VehicleRepository vehicleRepository;

    private final VisitorRepository visitorRepository;

    private final ComplaintRepository complaintRepository;

    private final EventRepository eventRepository;

    private final PollRepository pollRepository;

    private final LostFoundItemRepository
            lostFoundItemRepository;

    private final NoticeRepository noticeRepository;

    private final EmergencyRepository emergencyRepository;

    public DashboardServiceImpl(
            CommunityRepository communityRepository,
            ResidentRepository residentRepository,
            FlatRepository flatRepository,
            VehicleRepository vehicleRepository,
            VisitorRepository visitorRepository,
            ComplaintRepository complaintRepository,
            EventRepository eventRepository,
            PollRepository pollRepository,
            LostFoundItemRepository lostFoundItemRepository,
            NoticeRepository noticeRepository,
            EmergencyRepository emergencyRepository) {

        this.communityRepository =
                communityRepository;

        this.residentRepository =
                residentRepository;

        this.flatRepository =
                flatRepository;

        this.vehicleRepository =
                vehicleRepository;

        this.visitorRepository =
                visitorRepository;

        this.complaintRepository =
                complaintRepository;

        this.eventRepository =
                eventRepository;

        this.pollRepository =
                pollRepository;

        this.lostFoundItemRepository =
                lostFoundItemRepository;

        this.noticeRepository =
                noticeRepository;

        this.emergencyRepository =
                emergencyRepository;
    }

    @Override
    public DashboardResponse getCommunityDashboard(
            Long communityId) {

        // ==========================================
        // CHECK COMMUNITY
        // ==========================================

        Community community =
                communityRepository
                    .findById(communityId)
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Community not found with id: "
                            + communityId));

        DashboardResponse response =
                new DashboardResponse();

        response.setCommunityId(
                community.getCommunityId());

        response.setCommunityName(
                community.getName());

        // ==========================================
        // RESIDENT ANALYTICS
        // ==========================================

        response.setTotalResidents(
                residentRepository
                    .countByFlatBlockCommunityCommunityId(
                        communityId));

        response.setActiveResidents(
                residentRepository
                    .countByFlatBlockCommunityCommunityIdAndStatus(
                        communityId,
                        ResidentStatus.ACTIVE));

        // ==========================================
        // FLAT ANALYTICS
        // ==========================================

        response.setTotalFlats(
                flatRepository
                    .countByBlockCommunityCommunityId(
                        communityId));

        // ==========================================
        // VEHICLE ANALYTICS
        // ==========================================

        response.setTotalVehicles(
                vehicleRepository
                    .countByResidentFlatBlockCommunityCommunityId(
                        communityId));

        // ==========================================
        // VISITOR ANALYTICS
        // ==========================================

        response.setTotalVisitors(
                visitorRepository
                    .countByResidentFlatBlockCommunityCommunityId(
                        communityId));

        // ==========================================
        // COMPLAINT ANALYTICS
        // ==========================================

        response.setTotalComplaints(
                complaintRepository
                    .countByResidentFlatBlockCommunityCommunityId(
                        communityId));

        response.setPendingComplaints(
                complaintRepository
                    .countByResidentFlatBlockCommunityCommunityIdAndStatus(
                        communityId,
                        ComplaintStatus.OPEN));

        response.setResolvedComplaints(
                complaintRepository
                    .countByResidentFlatBlockCommunityCommunityIdAndStatus(
                        communityId,
                        ComplaintStatus.RESOLVED));

        // ==========================================
        // EVENT ANALYTICS
        // ==========================================

        response.setTotalEvents(
                eventRepository
                    .countByCommunityCommunityId(
                        communityId));

        response.setActiveEvents(
                eventRepository
                    .countByCommunityCommunityIdAndStatus(
                        communityId,
                        EventStatus.PUBLISHED));

        // ==========================================
        // POLL ANALYTICS
        // ==========================================

        response.setTotalPolls(
                pollRepository
                    .countByCommunityCommunityId(
                        communityId));

        response.setActivePolls(
                pollRepository
                    .countByCommunityCommunityIdAndStatus(
                        communityId,
                        PollStatus.ACTIVE));

        // ==========================================
        // LOST & FOUND ANALYTICS
        // ==========================================

        response.setTotalLostFoundItems(
                lostFoundItemRepository
                    .countByCommunityCommunityId(
                        communityId));

        // ==========================================
        // NOTICE ANALYTICS
        // ==========================================

        response.setTotalNotices(
                noticeRepository
                    .countByCommunityCommunityId(
                        communityId));

        // ==========================================
        // EMERGENCY ANALYTICS
        // ==========================================

        response.setTotalEmergencies(
                emergencyRepository
                    .countByCommunityCommunityId(
                        communityId));

        response.setActiveEmergencies(
                emergencyRepository
                    .countByCommunityCommunityIdAndStatus(
                        communityId,
                        EmergencyStatus.OPEN));

        return response;
    }
}