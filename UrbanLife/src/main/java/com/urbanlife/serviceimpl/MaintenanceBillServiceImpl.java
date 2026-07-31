package com.urbanlife.serviceimpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.urbanlife.dto.CreateMaintenanceBillRequest;
import com.urbanlife.dto.MaintenanceBillResponse;
import com.urbanlife.entity.Flat;
import com.urbanlife.entity.MaintenanceBill;
import com.urbanlife.enums.BillStatus;
import com.urbanlife.exception.DuplicateResourceException;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.repository.FlatRepository;
import com.urbanlife.repository.MaintenanceBillRepository;
import com.urbanlife.service.MaintenanceBillService;

@Service
public class MaintenanceBillServiceImpl
        implements MaintenanceBillService {

    private final MaintenanceBillRepository billRepository;
    private final FlatRepository flatRepository;
    private final CommunityRepository communityRepository;

    public MaintenanceBillServiceImpl(
            MaintenanceBillRepository billRepository,
            FlatRepository flatRepository,
            CommunityRepository communityRepository) {

        this.billRepository = billRepository;
        this.flatRepository = flatRepository;
        this.communityRepository = communityRepository;
    }

    @Override
    public MaintenanceBillResponse createBill(
            CreateMaintenanceBillRequest request) {

        Flat flat =
                flatRepository.findById(request.getFlatId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Flat not found with id: "
                            + request.getFlatId()));

        if (billRepository
                .existsByFlatFlatIdAndBillingYearAndBillingMonth(
                    request.getFlatId(),
                    request.getBillingYear(),
                    request.getBillingMonth())) {

            throw new DuplicateResourceException(
                    "Maintenance bill already exists for this flat and month");
        }

        MaintenanceBill bill =
                new MaintenanceBill();

        bill.setFlat(flat);

        bill.setBillingYear(
                request.getBillingYear());

        bill.setBillingMonth(
                request.getBillingMonth());

        bill.setAmount(
                request.getAmount());

        bill.setPaidAmount(
                BigDecimal.ZERO);

        bill.setBalanceAmount(
                request.getAmount());

        bill.setLateFee(
                BigDecimal.ZERO);

        bill.setDueDate(
                request.getDueDate());

        bill.setDescription(
                request.getDescription());

        bill.setStatus(
                BillStatus.PENDING);

        return mapToResponse(
                billRepository.save(bill));
    }

    @Override
    public MaintenanceBillResponse getBillById(
            Long billId) {

        return mapToResponse(findBill(billId));
    }

    @Override
    public List<MaintenanceBillResponse> getAllBills() {

        return mapList(billRepository.findAll());
    }

    @Override
    public List<MaintenanceBillResponse> getBillsByFlat(
            Long flatId) {

        if (!flatRepository.existsById(flatId)) {

            throw new ResourceNotFoundException(
                    "Flat not found with id: " + flatId);
        }

        return mapList(
                billRepository.findByFlatFlatId(flatId));
    }

    @Override
    public List<MaintenanceBillResponse> getBillsByCommunity(
            Long communityId) {

        validateCommunity(communityId);

        return mapList(
                billRepository
                    .findByFlatBlockCommunityCommunityId(
                        communityId));
    }

    @Override
    public List<MaintenanceBillResponse> getBillsByStatus(
            BillStatus status) {

        return mapList(
                billRepository.findByStatus(status));
    }

    @Override
    public List<MaintenanceBillResponse> getMonthlyBills(
            Long communityId,
            Integer year,
            Integer month) {

        validateCommunity(communityId);

        if (month < 1 || month > 12) {
            throw new IllegalArgumentException(
                    "Month must be between 1 and 12");
        }

        return mapList(
                billRepository
                    .findByFlatBlockCommunityCommunityIdAndBillingYearAndBillingMonth(
                        communityId,
                        year,
                        month));
    }

    @Override
    public MaintenanceBillResponse cancelBill(
            Long billId) {

        MaintenanceBill bill = findBill(billId);

        if (bill.getPaidAmount()
                .compareTo(BigDecimal.ZERO) > 0) {

            throw new IllegalArgumentException(
                    "Bill with existing payments cannot be cancelled");
        }

        if (bill.getStatus() == BillStatus.CANCELLED) {

            throw new IllegalArgumentException(
                    "Bill is already cancelled");
        }

        bill.setStatus(BillStatus.CANCELLED);

        return mapToResponse(
                billRepository.save(bill));
    }

    @Override
    public int markOverdueBills() {

        List<MaintenanceBill> bills =
                billRepository.findAll();

        int count = 0;

        for (MaintenanceBill bill : bills) {

            if ((bill.getStatus() == BillStatus.PENDING
                    || bill.getStatus()
                        == BillStatus.PARTIALLY_PAID)
                    && bill.getDueDate()
                        .isBefore(LocalDate.now())) {

                bill.setStatus(BillStatus.OVERDUE);

                billRepository.save(bill);

                count++;
            }
        }

        return count;
    }

    private MaintenanceBill findBill(Long id) {

        return billRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Maintenance bill not found with id: "
                        + id));
    }

    private void validateCommunity(Long id) {

        if (!communityRepository.existsById(id)) {

            throw new ResourceNotFoundException(
                    "Community not found with id: " + id);
        }
    }

    private List<MaintenanceBillResponse> mapList(
            List<MaintenanceBill> bills) {

        return bills.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private MaintenanceBillResponse mapToResponse(
            MaintenanceBill bill) {

        MaintenanceBillResponse response =
                new MaintenanceBillResponse();

        Flat flat = bill.getFlat();

        response.setBillId(bill.getBillId());

        response.setFlatId(flat.getFlatId());
        response.setFlatNumber(flat.getFlatNumber());

        response.setBlockId(
                flat.getBlock().getBlockId());

        response.setBlockName(
                flat.getBlock().getBlockName());

        response.setCommunityId(
                flat.getBlock()
                    .getCommunity()
                    .getCommunityId());

        response.setCommunityName(
                flat.getBlock()
                    .getCommunity()
                    .getName());

        response.setBillingYear(
                bill.getBillingYear());

        response.setBillingMonth(
                bill.getBillingMonth());

        response.setAmount(
                bill.getAmount());

        response.setPaidAmount(
                bill.getPaidAmount());

        response.setBalanceAmount(
                bill.getBalanceAmount());

        response.setLateFee(
                bill.getLateFee());

        response.setDueDate(
                bill.getDueDate());

        response.setStatus(
                bill.getStatus());

        response.setDescription(
                bill.getDescription());

        return response;
    }
}