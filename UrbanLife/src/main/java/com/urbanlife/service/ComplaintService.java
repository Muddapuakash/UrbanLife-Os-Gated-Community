package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.AssignComplaintRequest;
import com.urbanlife.dto.ComplaintResponse;
import com.urbanlife.dto.CreateComplaintRequest;
import com.urbanlife.dto.UpdateComplaintRequest;
import com.urbanlife.dto.UpdateComplaintStatusRequest;
import com.urbanlife.enums.ComplaintCategory;
import com.urbanlife.enums.ComplaintPriority;
import com.urbanlife.enums.ComplaintStatus;

public interface ComplaintService {

    ComplaintResponse createComplaint(
            CreateComplaintRequest request);

    ComplaintResponse getComplaintById(
            Long complaintId);

    List<ComplaintResponse> getAllComplaints();

    List<ComplaintResponse> getComplaintsByResident(
            Long residentId);

    List<ComplaintResponse> getComplaintsByCommunity(
            Long communityId);

    List<ComplaintResponse> getComplaintsByAssignedUser(
            Long userId);

    List<ComplaintResponse> getComplaintsByStatus(
            ComplaintStatus status);

    List<ComplaintResponse> getComplaintsByPriority(
            ComplaintPriority priority);

    List<ComplaintResponse> getComplaintsByCategory(
            ComplaintCategory category);

    ComplaintResponse updateComplaint(
            Long complaintId,
            UpdateComplaintRequest request);

    ComplaintResponse assignComplaint(
            Long complaintId,
            AssignComplaintRequest request);

    ComplaintResponse updateComplaintStatus(
            Long complaintId,
            UpdateComplaintStatusRequest request);

    void deleteComplaint(Long complaintId);
}