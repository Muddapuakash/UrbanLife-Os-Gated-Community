package com.urbanlife.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.urbanlife.entity.AmenityBooking;
import com.urbanlife.enums.BookingStatus;

public interface AmenityBookingRepository
        extends JpaRepository<AmenityBooking, Long> {

    List<AmenityBooking>
        findByResidentResidentId(Long residentId);

    List<AmenityBooking>
        findByAmenityAmenityId(Long amenityId);

    List<AmenityBooking>
        findByAmenityAmenityIdAndBookingDate(
            Long amenityId,
            LocalDate bookingDate);

    List<AmenityBooking>
        findByStatus(BookingStatus status);

    List<AmenityBooking>
        findByAmenityCommunityCommunityId(
            Long communityId);

    @Query("""
        SELECT COUNT(b)
        FROM AmenityBooking b
        WHERE b.amenity.amenityId = :amenityId
        AND b.bookingDate = :bookingDate
        AND b.status = com.urbanlife.enums.BookingStatus.CONFIRMED
        AND b.startTime < :endTime
        AND b.endTime > :startTime
    """)
    long countOverlappingBookings(
            @Param("amenityId") Long amenityId,
            @Param("bookingDate") LocalDate bookingDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
}