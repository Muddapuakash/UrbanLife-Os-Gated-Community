package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.StaffAttendanceResponse;
import com.urbanlife.dto.StaffEntryRequest;

public interface StaffAttendanceService {

    StaffAttendanceResponse recordEntry(
            Long staffId,
            StaffEntryRequest request);

    StaffAttendanceResponse recordExit(
            Long staffId,
            StaffEntryRequest request);

    List<StaffAttendanceResponse> getStaffHistory(
            Long staffId);

    List<StaffAttendanceResponse> getCurrentlyInside(
            Long communityId);
}