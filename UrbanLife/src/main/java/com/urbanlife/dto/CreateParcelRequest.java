package com.urbanlife.dto;

import com.urbanlife.enums.DeliveryProvider;
import com.urbanlife.enums.ParcelType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateParcelRequest {

    @NotNull
    private Long residentId;

    @NotNull
    private Long receivedByUserId;

    @NotNull
    private ParcelType parcelType;

    @NotNull
    private DeliveryProvider deliveryProvider;

    @Size(max = 100)
    private String providerName;

    @Size(max = 150)
    private String trackingNumber;

    @Size(max = 150)
    private String deliveryPersonName;

    @Size(max = 20)
    private String deliveryPersonPhone;

    @Size(max = 500)
    private String description;

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public Long getReceivedByUserId() {
        return receivedByUserId;
    }

    public void setReceivedByUserId(Long receivedByUserId) {
        this.receivedByUserId = receivedByUserId;
    }

    public ParcelType getParcelType() {
        return parcelType;
    }

    public void setParcelType(ParcelType parcelType) {
        this.parcelType = parcelType;
    }

    public DeliveryProvider getDeliveryProvider() {
        return deliveryProvider;
    }

    public void setDeliveryProvider(
            DeliveryProvider deliveryProvider) {
        this.deliveryProvider = deliveryProvider;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getDeliveryPersonName() {
        return deliveryPersonName;
    }

    public void setDeliveryPersonName(String deliveryPersonName) {
        this.deliveryPersonName = deliveryPersonName;
    }

    public String getDeliveryPersonPhone() {
        return deliveryPersonPhone;
    }

    public void setDeliveryPersonPhone(String deliveryPersonPhone) {
        this.deliveryPersonPhone = deliveryPersonPhone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}