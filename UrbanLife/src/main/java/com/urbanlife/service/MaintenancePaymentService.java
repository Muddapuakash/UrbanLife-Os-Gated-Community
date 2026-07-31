package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.CreatePaymentRequest;
import com.urbanlife.dto.MaintenancePaymentResponse;

public interface MaintenancePaymentService {

    MaintenancePaymentResponse makePayment(
            Long billId,
            CreatePaymentRequest request);

    MaintenancePaymentResponse getPaymentById(
            Long paymentId);

    List<MaintenancePaymentResponse> getPaymentsByBill(
            Long billId);

    List<MaintenancePaymentResponse> getPaymentsByFlat(
            Long flatId);

    List<MaintenancePaymentResponse> getPaymentsByCommunity(
            Long communityId);
}