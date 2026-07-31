package com.urbanlife.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.StaffAttendance;
import com.urbanlife.enums.AttendanceStatus;

public interface StaffAttendanceRepository
        extends JpaRepository<StaffAttendance, Long> {

    Optional<StaffAttendance>
        findFirstByStaffStaffIdAndStatusOrderByEntryTimeDesc(
            Long staffId,
            AttendanceStatus status);

    List<StaffAttendance>
        findByStaffStaffIdOrderByEntryTimeDesc(
            Long staffId);

    List<StaffAttendance>
        findByCommunityCommunityIdAndStatus(
            Long communityId,
            AttendanceStatus status);
}