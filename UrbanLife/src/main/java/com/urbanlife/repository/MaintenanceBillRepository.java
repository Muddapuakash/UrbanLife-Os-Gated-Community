package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.MaintenanceBill;
import com.urbanlife.enums.BillStatus;

public interface MaintenanceBillRepository
        extends JpaRepository<MaintenanceBill, Long> {

    boolean existsByFlatFlatIdAndBillingYearAndBillingMonth(
            Long flatId,
            Integer billingYear,
            Integer billingMonth);

    List<MaintenanceBill> findByFlatFlatId(Long flatId);

    List<MaintenanceBill> findByStatus(BillStatus status);

    List<MaintenanceBill>
        findByFlatBlockCommunityCommunityId(
            Long communityId);

    List<MaintenanceBill>
        findByFlatBlockCommunityCommunityIdAndBillingYearAndBillingMonth(
            Long communityId,
            Integer billingYear,
            Integer billingMonth);
}