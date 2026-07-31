package com.urbanlife.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.urbanlife.enums.PaymentMethod;

import jakarta.persistence.*;

@Entity
@Table(
    name = "maintenance_payments",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = "transaction_reference"
        )
    }
)
public class MaintenancePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false)
    private MaintenanceBill bill;

    @Column(
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(
        name = "transaction_reference",
        nullable = false,
        unique = true,
        length = 100
    )
    private String transactionReference;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Column(length = 500)
    private String remarks;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public MaintenancePayment() {
    }

    @PrePersist
    public void onCreate() {

        createdAt = LocalDateTime.now();

        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public MaintenanceBill getBill() {
        return bill;
    }

    public void setBill(MaintenanceBill bill) {
        this.bill = bill;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(
            PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(
            String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(
            LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}