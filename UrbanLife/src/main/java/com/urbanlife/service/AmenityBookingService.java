package com.urbanlife.service;

import java.time.LocalDate;
import java.util.List;

import com.urbanlife.dto.AmenityBookingResponse;
import com.urbanlife.dto.CancelBookingRequest;
import com.urbanlife.dto.CreateAmenityBookingRequest;

public interface AmenityBookingService {

    AmenityBookingResponse createBooking(
            CreateAmenityBookingRequest request);

    AmenityBookingResponse getBookingById(Long bookingId);

    List<AmenityBookingResponse> getAllBookings();

    List<AmenityBookingResponse> getByResident(
            Long residentId);

    List<AmenityBookingResponse> getByAmenity(
            Long amenityId);

    List<AmenityBookingResponse> getByAmenityAndDate(
            Long amenityId,
            LocalDate date);

    List<AmenityBookingResponse> getByCommunity(
            Long communityId);

    AmenityBookingResponse cancelBooking(
            Long bookingId,
            CancelBookingRequest request);

    AmenityBookingResponse completeBooking(
            Long bookingId);
}