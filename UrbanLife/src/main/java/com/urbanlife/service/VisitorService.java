package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.CreateVisitorRequest;
import com.urbanlife.dto.GateCheckInRequest;
import com.urbanlife.dto.GateCheckOutRequest;
import com.urbanlife.dto.VisitorApprovalRequest;
import com.urbanlife.dto.VisitorResponse;
import com.urbanlife.enums.VisitStatus;
import com.urbanlife.enums.VisitorType;

public interface VisitorService {

    VisitorResponse createVisitor(
            CreateVisitorRequest request);

    VisitorResponse getVisitorById(Long visitorId);

    VisitorResponse getVisitorByPassCode(String passCode);

    List<VisitorResponse> getAllVisitors();

    List<VisitorResponse> getVisitorsByResident(
            Long residentId);

    List<VisitorResponse> getVisitorsByFlat(Long flatId);

    List<VisitorResponse> getVisitorsByCommunity(
            Long communityId);

    List<VisitorResponse> getVisitorsByStatus(
            VisitStatus status);

    List<VisitorResponse> getVisitorsByType(
            VisitorType visitorType);

    VisitorResponse approveOrRejectVisitor(
            Long visitorId,
            VisitorApprovalRequest request);

    VisitorResponse checkIn(
            GateCheckInRequest request);

    VisitorResponse checkOut(
            Long visitorId,
            GateCheckOutRequest request);

    VisitorResponse cancelVisitor(Long visitorId);

    void deleteVisitor(Long visitorId);
}