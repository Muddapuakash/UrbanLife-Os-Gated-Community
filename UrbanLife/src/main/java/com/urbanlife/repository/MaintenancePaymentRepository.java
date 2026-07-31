package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.MaintenancePayment;

public interface MaintenancePaymentRepository
        extends JpaRepository<MaintenancePayment, Long> {

    boolean existsByTransactionReference(
            String transactionReference);

    List<MaintenancePayment>
        findByBillBillId(Long billId);

    List<MaintenancePayment>
        findByBillFlatFlatId(Long flatId);

    List<MaintenancePayment>
        findByBillFlatBlockCommunityCommunityId(
            Long communityId);
}