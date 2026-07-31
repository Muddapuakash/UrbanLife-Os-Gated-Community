package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.urbanlife.dto.CreateMaintenanceBillRequest;
import com.urbanlife.dto.MaintenanceBillResponse;
import com.urbanlife.enums.BillStatus;
import com.urbanlife.service.MaintenanceBillService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/maintenance-bills")
public class MaintenanceBillController {

    private final MaintenanceBillService billService;

    public MaintenanceBillController(
            MaintenanceBillService billService) {

        this.billService = billService;
    }

    // =====================================================
    // GENERATE BILL
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public ResponseEntity<MaintenanceBillResponse> create(
            @Valid @RequestBody
            CreateMaintenanceBillRequest request) {

        return new ResponseEntity<>(
                billService.createBill(request),
                HttpStatus.CREATED);
    }

    // =====================================================
    // GET ALL BILLS
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<MaintenanceBillResponse>>
            getAll() {

        return ResponseEntity.ok(
                billService.getAllBills());
    }

    // =====================================================
    // GET BILL BY ID
    // SUPER ADMIN + ADMIN + RESIDENT (own)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @GetMapping("/{billId}")
    public ResponseEntity<MaintenanceBillResponse> getById(
            @PathVariable Long billId) {

        return ResponseEntity.ok(
                billService.getBillById(billId));
    }

    // =====================================================
    // GET BILLS BY FLAT
    // SUPER ADMIN + ADMIN + RESIDENT (own flat)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @GetMapping("/flat/{flatId}")
    public ResponseEntity<List<MaintenanceBillResponse>>
            getByFlat(@PathVariable Long flatId) {

        return ResponseEntity.ok(
                billService.getBillsByFlat(flatId));
    }

    // =====================================================
    // GET BILLS BY COMMUNITY
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<MaintenanceBillResponse>>
            getByCommunity(
                    @PathVariable Long communityId) {

        return ResponseEntity.ok(
                billService.getBillsByCommunity(
                    communityId));
    }

    // =====================================================
    // GET BILLS BY STATUS
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/status")
    public ResponseEntity<List<MaintenanceBillResponse>>
            getByStatus(
                    @RequestParam BillStatus status) {

        return ResponseEntity.ok(
                billService.getBillsByStatus(status));
    }

    // =====================================================
    // GET MONTHLY BILLS
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/community/{communityId}/month")
    public ResponseEntity<List<MaintenanceBillResponse>>
            getMonthlyBills(
                    @PathVariable Long communityId,
                    @RequestParam Integer year,
                    @RequestParam Integer month) {

        return ResponseEntity.ok(
                billService.getMonthlyBills(
                    communityId,
                    year,
                    month));
    }

    // =====================================================
    // CANCEL BILL
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PatchMapping("/{billId}/cancel")
    public ResponseEntity<MaintenanceBillResponse> cancel(
            @PathVariable Long billId) {

        return ResponseEntity.ok(
                billService.cancelBill(billId));
    }

    // =====================================================
    // MARK OVERDUE (system batch)
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PatchMapping("/mark-overdue")
    public ResponseEntity<String> markOverdue() {

        int count = billService.markOverdueBills();

        return ResponseEntity.ok(
                count + " bill(s) marked as overdue");
    }
}