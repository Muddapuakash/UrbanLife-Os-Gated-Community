package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.urbanlife.dto.CreatePaymentRequest;
import com.urbanlife.dto.MaintenancePaymentResponse;
import com.urbanlife.service.MaintenancePaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/maintenance-payments")
public class MaintenancePaymentController {

    private final MaintenancePaymentService paymentService;

    public MaintenancePaymentController(
            MaintenancePaymentService paymentService) {

        this.paymentService = paymentService;
    }

    // =====================================================
    // MAKE PAYMENT
    // SUPER ADMIN + ADMIN + RESIDENT
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @PostMapping("/bill/{billId}")
    public ResponseEntity<MaintenancePaymentResponse>
            makePayment(
                    @PathVariable Long billId,
                    @Valid @RequestBody
                    CreatePaymentRequest request) {

        return new ResponseEntity<>(
                paymentService.makePayment(
                    billId,
                    request),
                HttpStatus.CREATED);
    }

    // =====================================================
    // GET PAYMENT BY ID
    // SUPER ADMIN + ADMIN + RESIDENT (own)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @GetMapping("/{paymentId}")
    public ResponseEntity<MaintenancePaymentResponse>
            getById(@PathVariable Long paymentId) {

        return ResponseEntity.ok(
                paymentService
                    .getPaymentById(paymentId));
    }

    // =====================================================
    // GET PAYMENTS BY BILL
    // SUPER ADMIN + ADMIN + RESIDENT (own)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @GetMapping("/bill/{billId}")
    public ResponseEntity<List<MaintenancePaymentResponse>>
            getByBill(@PathVariable Long billId) {

        return ResponseEntity.ok(
                paymentService
                    .getPaymentsByBill(billId));
    }

    // =====================================================
    // GET PAYMENTS BY FLAT
    // SUPER ADMIN + ADMIN + RESIDENT (own flat)
    // =====================================================

    @PreAuthorize(
        "hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RESIDENT')"
    )
    @GetMapping("/flat/{flatId}")
    public ResponseEntity<List<MaintenancePaymentResponse>>
            getByFlat(@PathVariable Long flatId) {

        return ResponseEntity.ok(
                paymentService
                    .getPaymentsByFlat(flatId));
    }

    // =====================================================
    // GET PAYMENTS BY COMMUNITY
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<MaintenancePaymentResponse>>
            getByCommunity(
                    @PathVariable Long communityId) {

        return ResponseEntity.ok(
                paymentService
                    .getPaymentsByCommunity(communityId));
    }
}