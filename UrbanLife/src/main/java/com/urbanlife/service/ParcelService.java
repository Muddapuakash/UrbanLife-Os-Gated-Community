package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.CollectParcelRequest;
import com.urbanlife.dto.CreateParcelRequest;
import com.urbanlife.dto.ParcelResponse;
import com.urbanlife.dto.ReturnParcelRequest;
import com.urbanlife.enums.ParcelStatus;
import com.urbanlife.enums.ParcelType;

public interface ParcelService {

    ParcelResponse createParcel(
            CreateParcelRequest request);

    ParcelResponse getParcelById(
            Long parcelId);

    List<ParcelResponse> getAllParcels();

    List<ParcelResponse> getByResident(
            Long residentId);

    List<ParcelResponse> getByFlat(
            Long flatId);

    List<ParcelResponse> getByCommunity(
            Long communityId);

    List<ParcelResponse> getByStatus(
            Long communityId,
            ParcelStatus status);

    List<ParcelResponse> getByType(
            Long communityId,
            ParcelType parcelType);

    ParcelResponse markAsNotified(
            Long parcelId);

    ParcelResponse collectParcel(
            Long parcelId,
            CollectParcelRequest request);

    ParcelResponse returnParcel(
            Long parcelId,
            ReturnParcelRequest request);

    long getPendingParcelCount(
            Long communityId);
}