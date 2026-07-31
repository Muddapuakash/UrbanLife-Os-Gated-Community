package com.urbanlife.serviceimpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urbanlife.dto.CreatePaymentRequest;
import com.urbanlife.dto.MaintenancePaymentResponse;
import com.urbanlife.entity.MaintenanceBill;
import com.urbanlife.entity.MaintenancePayment;
import com.urbanlife.enums.BillStatus;
import com.urbanlife.exception.DuplicateResourceException;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.repository.FlatRepository;
import com.urbanlife.repository.MaintenanceBillRepository;
import com.urbanlife.repository.MaintenancePaymentRepository;
import com.urbanlife.service.MaintenancePaymentService;

@Service
public class MaintenancePaymentServiceImpl
        implements MaintenancePaymentService {

    private final MaintenancePaymentRepository paymentRepository;
    private final MaintenanceBillRepository billRepository;
    private final FlatRepository flatRepository;
    private final CommunityRepository communityRepository;

    public MaintenancePaymentServiceImpl(
            MaintenancePaymentRepository paymentRepository,
            MaintenanceBillRepository billRepository,
            FlatRepository flatRepository,
            CommunityRepository communityRepository) {

        this.paymentRepository = paymentRepository;
        this.billRepository = billRepository;
        this.flatRepository = flatRepository;
        this.communityRepository = communityRepository;
    }

    @Override
    @Transactional
    public MaintenancePaymentResponse makePayment(
            Long billId,
            CreatePaymentRequest request) {

        MaintenanceBill bill =
                billRepository.findById(billId)
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Maintenance bill not found with id: "
                            + billId));

        if (bill.getStatus() == BillStatus.PAID) {

            throw new IllegalArgumentException(
                    "Bill is already fully paid");
        }

        if (bill.getStatus() == BillStatus.CANCELLED) {

            throw new IllegalArgumentException(
                    "Cannot pay a cancelled bill");
        }

        if (request.getAmount()
                .compareTo(bill.getBalanceAmount()) > 0) {

            throw new IllegalArgumentException(
                    "Payment cannot exceed outstanding balance");
        }

        String transactionReference =
                request.getTransactionReference();

        if (transactionReference == null
                || transactionReference.isBlank()) {

            transactionReference =
                    "PAY-"
                    + UUID.randomUUID()
                        .toString()
                        .substring(0, 10)
                        .toUpperCase();
        }

        if (paymentRepository
                .existsByTransactionReference(
                    transactionReference)) {

            throw new DuplicateResourceException(
                    "Transaction reference already exists");
        }

        MaintenancePayment payment =
                new MaintenancePayment();

        payment.setBill(bill);

        payment.setAmount(
                request.getAmount());

        payment.setPaymentMethod(
                request.getPaymentMethod());

        payment.setTransactionReference(
                transactionReference);

        payment.setPaymentDate(
                LocalDateTime.now());

        payment.setRemarks(
                request.getRemarks());

        MaintenancePayment saved =
                paymentRepository.save(payment);

        BigDecimal newPaidAmount =
                bill.getPaidAmount()
                    .add(request.getAmount());

        BigDecimal newBalance =
                bill.getAmount()
                    .add(bill.getLateFee())
                    .subtract(newPaidAmount);

        bill.setPaidAmount(newPaidAmount);
        bill.setBalanceAmount(newBalance);

        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {

            bill.setStatus(BillStatus.PAID);
            bill.setPaidAt(LocalDateTime.now());

        } else {

            bill.setStatus(
                    BillStatus.PARTIALLY_PAID);
        }

        billRepository.save(bill);

        return mapToResponse(saved);
    }

    @Override
    public MaintenancePaymentResponse getPaymentById(
            Long paymentId) {

        MaintenancePayment payment =
                paymentRepository.findById(paymentId)
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Payment not found with id: "
                            + paymentId));

        return mapToResponse(payment);
    }

    @Override
    public List<MaintenancePaymentResponse> getPaymentsByBill(
            Long billId) {

        if (!billRepository.existsById(billId)) {

            throw new ResourceNotFoundException(
                    "Bill not found with id: " + billId);
        }

        return mapList(
                paymentRepository.findByBillBillId(billId));
    }

    @Override
    public List<MaintenancePaymentResponse> getPaymentsByFlat(
            Long flatId) {

        if (!flatRepository.existsById(flatId)) {

            throw new ResourceNotFoundException(
                    "Flat not found with id: " + flatId);
        }

        return mapList(
                paymentRepository
                    .findByBillFlatFlatId(flatId));
    }

    @Override
    public List<MaintenancePaymentResponse>
            getPaymentsByCommunity(Long communityId) {

        if (!communityRepository.existsById(communityId)) {

            throw new ResourceNotFoundException(
                    "Community not found with id: "
                            + communityId);
        }

        return mapList(
                paymentRepository
                    .findByBillFlatBlockCommunityCommunityId(
                        communityId));
    }

    private List<MaintenancePaymentResponse> mapList(
            List<MaintenancePayment> payments) {

        return payments.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private MaintenancePaymentResponse mapToResponse(
            MaintenancePayment payment) {

        MaintenancePaymentResponse response =
                new MaintenancePaymentResponse();

        response.setPaymentId(
                payment.getPaymentId());

        response.setBillId(
                payment.getBill().getBillId());

        response.setAmount(
                payment.getAmount());

        response.setPaymentMethod(
                payment.getPaymentMethod());

        response.setTransactionReference(
                payment.getTransactionReference());

        response.setPaymentDate(
                payment.getPaymentDate());

        response.setRemarks(
                payment.getRemarks());

        return response;
    }
}