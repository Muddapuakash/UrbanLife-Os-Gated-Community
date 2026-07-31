package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.*;
import com.urbanlife.enums.StaffStatus;
import com.urbanlife.enums.StaffType;
import com.urbanlife.enums.VerificationStatus;

public interface DomesticStaffService {

    DomesticStaffResponse createStaff(
            CreateDomesticStaffRequest request);

    DomesticStaffResponse getStaffById(
            Long staffId);

    List<DomesticStaffResponse> getByCommunity(
            Long communityId);

    List<DomesticStaffResponse> getByType(
            Long communityId,
            StaffType type);

    List<DomesticStaffResponse> getByStatus(
            Long communityId,
            StaffStatus status);

    List<DomesticStaffResponse> getByVerificationStatus(
            Long communityId,
            VerificationStatus status);

    DomesticStaffResponse verifyStaff(
            Long staffId,
            VerifyStaffRequest request);

    DomesticStaffResponse blockStaff(
            Long staffId,
            BlockStaffRequest request);

    DomesticStaffResponse activateStaff(
            Long staffId);

    StaffAssignmentResponse assignToResident(
            Long staffId,
            StaffAssignmentRequest request);

    List<StaffAssignmentResponse> getStaffAssignments(
            Long staffId);

    void removeAssignment(Long assignmentId);

    void deleteStaff(Long staffId);
}