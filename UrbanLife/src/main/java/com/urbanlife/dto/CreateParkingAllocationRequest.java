package com.urbanlife.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public class CreateParkingAllocationRequest {

    @NotNull(message = "Vehicle id is required")
    private Long vehicleId;

    @NotNull(message = "Parking slot id is required")
    private Long parkingSlotId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getParkingSlotId() {
        return parkingSlotId;
    }

    public void setParkingSlotId(Long parkingSlotId) {
        this.parkingSlotId = parkingSlotId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
}