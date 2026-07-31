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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.urbanlife.dto.AssignComplaintRequest;
import com.urbanlife.dto.ComplaintResponse;
import com.urbanlife.dto.CreateComplaintRequest;
import com.urbanlife.dto.UpdateComplaintRequest;
import com.urbanlife.dto.UpdateComplaintStatusRequest;
import com.urbanlife.enums.ComplaintCategory;
import com.urbanlife.enums.ComplaintPriority;
import com.urbanlife.enums.ComplaintStatus;
import com.urbanlife.service.ComplaintService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(
            ComplaintService complaintService) {

        this.complaintService = complaintService;
    }

    // =====================================================
    // CREATE COMPLAINT
    // RESIDENT + ADMIN + SUPER ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @PostMapping
    public ResponseEntity<ComplaintResponse> createComplaint(
            @Valid @RequestBody CreateComplaintRequest request) {

        return new ResponseEntity<>(
                complaintService.createComplaint(request),
                HttpStatus.CREATED);
    }

    // =====================================================
    // GET ALL COMPLAINTS
    // ADMIN + SUPER ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN')"
    )
    @GetMapping
    public ResponseEntity<List<ComplaintResponse>>
            getAllComplaints() {

        return ResponseEntity.ok(
                complaintService.getAllComplaints());
    }

    // =====================================================
    // GET COMPLAINT BY ID
    // ADMIN + SUPER ADMIN + RESIDENT + STAFF
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'STAFF')"
    )
    @GetMapping("/{complaintId}")
    public ResponseEntity<ComplaintResponse>
            getComplaintById(
                    @PathVariable Long complaintId) {

        return ResponseEntity.ok(
                complaintService
                    .getComplaintById(complaintId));
    }

    // =====================================================
    // GET COMPLAINTS BY RESIDENT
    // ADMIN + SUPER ADMIN + RESIDENT
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @GetMapping("/resident/{residentId}")
    public ResponseEntity<List<ComplaintResponse>>
            getByResident(
                    @PathVariable Long residentId) {

        return ResponseEntity.ok(
                complaintService
                    .getComplaintsByResident(residentId));
    }

    // =====================================================
    // GET COMPLAINTS BY COMMUNITY
    // ADMIN + SUPER ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN')"
    )
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<ComplaintResponse>>
            getByCommunity(
                    @PathVariable Long communityId) {

        return ResponseEntity.ok(
                complaintService
                    .getComplaintsByCommunity(communityId));
    }

    // =====================================================
    // GET COMPLAINTS ASSIGNED TO USER
    // ADMIN + SUPER ADMIN + STAFF
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'STAFF')"
    )
    @GetMapping("/assigned/{userId}")
    public ResponseEntity<List<ComplaintResponse>>
            getByAssignedUser(
                    @PathVariable Long userId) {

        return ResponseEntity.ok(
                complaintService
                    .getComplaintsByAssignedUser(userId));
    }

    // =====================================================
    // SEARCH BY STATUS
    // ADMIN + SUPER ADMIN + STAFF
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'STAFF')"
    )
    @GetMapping("/search/status")
    public ResponseEntity<List<ComplaintResponse>>
            getByStatus(
                    @RequestParam ComplaintStatus status) {

        return ResponseEntity.ok(
                complaintService
                    .getComplaintsByStatus(status));
    }

    // =====================================================
    // SEARCH BY PRIORITY
    // ADMIN + SUPER ADMIN + STAFF
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'STAFF')"
    )
    @GetMapping("/search/priority")
    public ResponseEntity<List<ComplaintResponse>>
            getByPriority(
                    @RequestParam ComplaintPriority priority) {

        return ResponseEntity.ok(
                complaintService
                    .getComplaintsByPriority(priority));
    }

    // =====================================================
    // SEARCH BY CATEGORY
    // ADMIN + SUPER ADMIN + STAFF
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'STAFF')"
    )
    @GetMapping("/search/category")
    public ResponseEntity<List<ComplaintResponse>>
            getByCategory(
                    @RequestParam ComplaintCategory category) {

        return ResponseEntity.ok(
                complaintService
                    .getComplaintsByCategory(category));
    }

    // =====================================================
    // UPDATE COMPLAINT DETAILS
    // RESIDENT + ADMIN + SUPER ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @PutMapping("/{complaintId}")
    public ResponseEntity<ComplaintResponse>
            updateComplaint(
                    @PathVariable Long complaintId,
                    @Valid @RequestBody
                    UpdateComplaintRequest request) {

        return ResponseEntity.ok(
                complaintService.updateComplaint(
                        complaintId,
                        request));
    }

    // =====================================================
    // ASSIGN COMPLAINT TO STAFF
    // ADMIN + SUPER ADMIN ONLY
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN')"
    )
    @PatchMapping("/{complaintId}/assign")
    public ResponseEntity<ComplaintResponse>
            assignComplaint(
                    @PathVariable Long complaintId,
                    @Valid @RequestBody
                    AssignComplaintRequest request) {

        return ResponseEntity.ok(
                complaintService.assignComplaint(
                        complaintId,
                        request));
    }

    // =====================================================
    // UPDATE COMPLAINT STATUS
    // ADMIN + SUPER ADMIN + STAFF
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'STAFF')"
    )
    @PatchMapping("/{complaintId}/status")
    public ResponseEntity<ComplaintResponse>
            updateStatus(
                    @PathVariable Long complaintId,
                    @Valid @RequestBody
                    UpdateComplaintStatusRequest request) {

        return ResponseEntity.ok(
                complaintService.updateComplaintStatus(
                        complaintId,
                        request));
    }

    // =====================================================
    // DELETE COMPLAINT
    // ADMIN + SUPER ADMIN
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN')"
    )
    @DeleteMapping("/{complaintId}")
    public ResponseEntity<Void> deleteComplaint(
            @PathVariable Long complaintId) {

        complaintService.deleteComplaint(complaintId);

        return ResponseEntity.noContent().build();
    }
}