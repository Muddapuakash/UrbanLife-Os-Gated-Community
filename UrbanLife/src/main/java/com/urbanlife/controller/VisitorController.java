package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.urbanlife.dto.CreateVisitorRequest;
import com.urbanlife.dto.GateCheckInRequest;
import com.urbanlife.dto.GateCheckOutRequest;
import com.urbanlife.dto.VisitorApprovalRequest;
import com.urbanlife.dto.VisitorResponse;
import com.urbanlife.enums.VisitStatus;
import com.urbanlife.enums.VisitorType;
import com.urbanlife.service.VisitorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/visitors")
public class VisitorController {

    private final VisitorService visitorService;

    public VisitorController(
            VisitorService visitorService) {

        this.visitorService = visitorService;
    }

    // =====================================================
    // CREATE VISITOR
    // RESIDENT + ADMIN + SUPER ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @PostMapping
    public ResponseEntity<VisitorResponse> createVisitor(
            @Valid @RequestBody CreateVisitorRequest request) {

        return new ResponseEntity<>(
                visitorService.createVisitor(request),
                HttpStatus.CREATED);
    }

    // =====================================================
    // GET ALL VISITORS
    // ADMIN + SUPER ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping
    public ResponseEntity<List<VisitorResponse>>
            getAllVisitors() {

        return ResponseEntity.ok(
                visitorService.getAllVisitors());
    }

    // =====================================================
    // GET VISITOR BY ID
    // ADMIN + SUPER ADMIN + RESIDENT + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY')"
    )
    @GetMapping("/{visitorId}")
    public ResponseEntity<VisitorResponse> getVisitorById(
            @PathVariable Long visitorId) {

        return ResponseEntity.ok(
                visitorService.getVisitorById(visitorId));
    }

    // =====================================================
    // GET VISITOR BY PASS CODE
    // SECURITY + ADMIN + SUPER ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping("/pass/{passCode}")
    public ResponseEntity<VisitorResponse> getByPassCode(
            @PathVariable String passCode) {

        return ResponseEntity.ok(
                visitorService.getVisitorByPassCode(passCode));
    }

    // =====================================================
    // GET VISITORS BY RESIDENT
    // RESIDENT + ADMIN + SUPER ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @GetMapping("/resident/{residentId}")
    public ResponseEntity<List<VisitorResponse>>
            getByResident(
                    @PathVariable Long residentId) {

        return ResponseEntity.ok(
                visitorService
                    .getVisitorsByResident(residentId));
    }

    // =====================================================
    // GET VISITORS BY FLAT
    // ADMIN + SUPER ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping("/flat/{flatId}")
    public ResponseEntity<List<VisitorResponse>>
            getByFlat(
                    @PathVariable Long flatId) {

        return ResponseEntity.ok(
                visitorService.getVisitorsByFlat(flatId));
    }

    // =====================================================
    // GET VISITORS BY COMMUNITY
    // ADMIN + SUPER ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<VisitorResponse>>
            getByCommunity(
                    @PathVariable Long communityId) {

        return ResponseEntity.ok(
                visitorService
                    .getVisitorsByCommunity(communityId));
    }

    // =====================================================
    // SEARCH VISITORS BY STATUS
    // ADMIN + SUPER ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping("/search/status")
    public ResponseEntity<List<VisitorResponse>>
            getByStatus(
                    @RequestParam VisitStatus status) {

        return ResponseEntity.ok(
                visitorService.getVisitorsByStatus(status));
    }

    // =====================================================
    // SEARCH VISITORS BY TYPE
    // ADMIN + SUPER ADMIN + SECURITY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @GetMapping("/search/type")
    public ResponseEntity<List<VisitorResponse>>
            getByType(
                    @RequestParam VisitorType type) {

        return ResponseEntity.ok(
                visitorService.getVisitorsByType(type));
    }

    // =====================================================
    // APPROVE / REJECT VISITOR
    // RESIDENT + ADMIN + SUPER ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @PatchMapping("/{visitorId}/approval")
    public ResponseEntity<VisitorResponse>
            approveOrReject(
                    @PathVariable Long visitorId,
                    @Valid @RequestBody
                    VisitorApprovalRequest request) {

        return ResponseEntity.ok(
                visitorService.approveOrRejectVisitor(
                        visitorId,
                        request));
    }

    // =====================================================
    // GATE CHECK-IN
    // SECURITY + ADMIN + SUPER ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @PatchMapping("/check-in")
    public ResponseEntity<VisitorResponse> checkIn(
            @Valid @RequestBody GateCheckInRequest request) {

        return ResponseEntity.ok(
                visitorService.checkIn(request));
    }

    // =====================================================
    // GATE CHECK-OUT
    // SECURITY + ADMIN + SUPER ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SECURITY')"
    )
    @PatchMapping("/{visitorId}/check-out")
    public ResponseEntity<VisitorResponse> checkOut(
            @PathVariable Long visitorId,
            @Valid @RequestBody
            GateCheckOutRequest request) {

        return ResponseEntity.ok(
                visitorService.checkOut(
                        visitorId,
                        request));
    }

    // =====================================================
    // CANCEL VISITOR
    // RESIDENT + ADMIN + SUPER ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @PatchMapping("/{visitorId}/cancel")
    public ResponseEntity<VisitorResponse> cancelVisitor(
            @PathVariable Long visitorId) {

        return ResponseEntity.ok(
                visitorService.cancelVisitor(visitorId));
    }

    // =====================================================
    // DELETE VISITOR RECORD
    // ADMIN + SUPER ADMIN ONLY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN')"
    )
    @DeleteMapping("/{visitorId}")
    public ResponseEntity<Void> deleteVisitor(
            @PathVariable Long visitorId) {

        visitorService.deleteVisitor(visitorId);

        return ResponseEntity.noContent().build();
    }
}