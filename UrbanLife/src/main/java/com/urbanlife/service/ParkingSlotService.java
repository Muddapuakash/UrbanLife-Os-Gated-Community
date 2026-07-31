package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.CreateParkingSlotRequest;
import com.urbanlife.dto.ParkingSlotResponse;
import com.urbanlife.enums.ParkingSlotStatus;

public interface ParkingSlotService {

    ParkingSlotResponse createParkingSlot(
            CreateParkingSlotRequest request);

    ParkingSlotResponse getParkingSlotById(Long id);

    List<ParkingSlotResponse> getAllParkingSlots();

    List<ParkingSlotResponse> getSlotsByCommunity(
            Long communityId);

    List<ParkingSlotResponse> getSlotsByStatus(
            Long communityId,
            ParkingSlotStatus status);
}