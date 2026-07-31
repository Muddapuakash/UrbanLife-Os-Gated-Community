package com.urbanlife.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urbanlife.dto.StaffAttendanceResponse;
import com.urbanlife.dto.StaffEntryRequest;
import com.urbanlife.entity.DomesticStaff;
import com.urbanlife.entity.StaffAttendance;
import com.urbanlife.entity.User;
import com.urbanlife.enums.AttendanceStatus;
import com.urbanlife.enums.StaffStatus;
import com.urbanlife.enums.VerificationStatus;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.DomesticStaffRepository;
import com.urbanlife.repository.StaffAttendanceRepository;
import com.urbanlife.repository.UserRepository;
import com.urbanlife.service.StaffAttendanceService;

@Service
public class StaffAttendanceServiceImpl
        implements StaffAttendanceService {

    private final StaffAttendanceRepository attendanceRepository;
    private final DomesticStaffRepository staffRepository;
    private final UserRepository userRepository;

    public StaffAttendanceServiceImpl(
            StaffAttendanceRepository attendanceRepository,
            DomesticStaffRepository staffRepository,
            UserRepository userRepository) {

        this.attendanceRepository = attendanceRepository;
        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public StaffAttendanceResponse recordEntry(
            Long staffId,
            StaffEntryRequest request) {

        DomesticStaff staff = findStaff(staffId);

        if (staff.getStatus() != StaffStatus.ACTIVE) {
            throw new IllegalArgumentException(
                "Staff is not ACTIVE");
        }

        if (staff.getVerificationStatus()
                != VerificationStatus.VERIFIED) {

            throw new IllegalArgumentException(
                "Only VERIFIED staff can enter community");
        }

        boolean alreadyInside =
            attendanceRepository
                .findFirstByStaffStaffIdAndStatusOrderByEntryTimeDesc(
                    staffId,
                    AttendanceStatus.INSIDE)
                .isPresent();

        if (alreadyInside) {
            throw new IllegalArgumentException(
                "Staff is already inside the community");
        }

        User user = findUser(
            request.getRecordedByUserId());

        StaffAttendance attendance =
            new StaffAttendance();

        attendance.setStaff(staff);
        attendance.setCommunity(
            staff.getCommunity());

        attendance.setEntryTime(
            LocalDateTime.now());

        attendance.setStatus(
            AttendanceStatus.INSIDE);

        attendance.setEntryRecordedBy(user);

        return map(
            attendanceRepository.save(attendance));
    }

    @Override
    @Transactional
    public StaffAttendanceResponse recordExit(
            Long staffId,
            StaffEntryRequest request) {

        StaffAttendance attendance =
            attendanceRepository
                .findFirstByStaffStaffIdAndStatusOrderByEntryTimeDesc(
                    staffId,
                    AttendanceStatus.INSIDE)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Staff is not currently inside"));

        User user = findUser(
            request.getRecordedByUserId());

        attendance.setExitTime(
            LocalDateTime.now());

        attendance.setExitRecordedBy(user);

        attendance.setStatus(
            AttendanceStatus.EXITED);

        return map(
            attendanceRepository.save(attendance));
    }

    @Override
    public List<StaffAttendanceResponse>
            getStaffHistory(Long staffId) {

        findStaff(staffId);

        return attendanceRepository
            .findByStaffStaffIdOrderByEntryTimeDesc(
                staffId)
            .stream()
            .map(this::map)
            .toList();
    }

    @Override
    public List<StaffAttendanceResponse>
            getCurrentlyInside(Long communityId) {

        return attendanceRepository
            .findByCommunityCommunityIdAndStatus(
                communityId,
                AttendanceStatus.INSIDE)
            .stream()
            .map(this::map)
            .toList();
    }

    private DomesticStaff findStaff(Long id) {

        return staffRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Staff not found with id: " + id));
    }

    private User findUser(Long id) {

        return userRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "User not found with id: " + id));
    }

    private StaffAttendanceResponse map(
            StaffAttendance attendance) {

        StaffAttendanceResponse response =
            new StaffAttendanceResponse();

        response.setAttendanceId(
            attendance.getAttendanceId());

        response.setStaffId(
            attendance.getStaff().getStaffId());

        response.setStaffName(
            attendance.getStaff().getName());

        response.setCommunityId(
            attendance.getCommunity()
                .getCommunityId());

        response.setEntryTime(
            attendance.getEntryTime());

        response.setExitTime(
            attendance.getExitTime());

        response.setStatus(
            attendance.getStatus());

        if (attendance.getEntryRecordedBy() != null) {

            User user =
                attendance.getEntryRecordedBy();

            response.setEntryRecordedById(
                user.getUserId());

            response.setEntryRecordedByName(
                user.getFirstName()
                + " "
                + user.getLastName());
        }

        if (attendance.getExitRecordedBy() != null) {

            User user =
                attendance.getExitRecordedBy();

            response.setExitRecordedById(
                user.getUserId());

            response.setExitRecordedByName(
                user.getFirstName()
                + " "
                + user.getLastName());
        }

        return response;
    }
}