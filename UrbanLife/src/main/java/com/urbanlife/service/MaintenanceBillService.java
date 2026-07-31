package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.CreateMaintenanceBillRequest;
import com.urbanlife.dto.MaintenanceBillResponse;
import com.urbanlife.enums.BillStatus;

public interface MaintenanceBillService {

    MaintenanceBillResponse createBill(
            CreateMaintenanceBillRequest request);

    MaintenanceBillResponse getBillById(Long billId);

    List<MaintenanceBillResponse> getAllBills();

    List<MaintenanceBillResponse> getBillsByFlat(
            Long flatId);

    List<MaintenanceBillResponse> getBillsByCommunity(
            Long communityId);

    List<MaintenanceBillResponse> getBillsByStatus(
            BillStatus status);

    List<MaintenanceBillResponse> getMonthlyBills(
            Long communityId,
            Integer year,
            Integer month);

    MaintenanceBillResponse cancelBill(Long billId);

    int markOverdueBills();
}