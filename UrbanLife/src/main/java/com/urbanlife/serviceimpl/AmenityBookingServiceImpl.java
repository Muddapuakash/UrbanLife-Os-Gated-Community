package com.urbanlife.serviceimpl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.urbanlife.dto.AmenityBookingResponse;
import com.urbanlife.dto.CancelBookingRequest;
import com.urbanlife.dto.CreateAmenityBookingRequest;
import com.urbanlife.entity.Amenity;
import com.urbanlife.entity.AmenityBooking;
import com.urbanlife.entity.Resident;
import com.urbanlife.enums.AmenityStatus;
import com.urbanlife.enums.BookingStatus;
import com.urbanlife.enums.ResidentStatus;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.AmenityBookingRepository;
import com.urbanlife.repository.AmenityRepository;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.repository.ResidentRepository;
import com.urbanlife.service.AmenityBookingService;

@Service
public class AmenityBookingServiceImpl
        implements AmenityBookingService {

    private final AmenityBookingRepository bookingRepository;
    private final AmenityRepository amenityRepository;
    private final ResidentRepository residentRepository;
    private final CommunityRepository communityRepository;

    public AmenityBookingServiceImpl(
            AmenityBookingRepository bookingRepository,
            AmenityRepository amenityRepository,
            ResidentRepository residentRepository,
            CommunityRepository communityRepository) {

        this.bookingRepository = bookingRepository;
        this.amenityRepository = amenityRepository;
        this.residentRepository = residentRepository;
        this.communityRepository = communityRepository;
    }

    @Override
    public AmenityBookingResponse createBooking(
            CreateAmenityBookingRequest request) {

        Amenity amenity =
                amenityRepository
                    .findById(request.getAmenityId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Amenity not found with id: "
                            + request.getAmenityId()));

        Resident resident =
                residentRepository
                    .findById(request.getResidentId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Resident not found with id: "
                            + request.getResidentId()));

        // Rule 1
        if (resident.getStatus() != ResidentStatus.ACTIVE) {

            throw new IllegalArgumentException(
                    "Only active residents can book amenities");
        }

        // Rule 2
        if (amenity.getStatus()
                != AmenityStatus.AVAILABLE) {

            throw new IllegalArgumentException(
                    "Amenity is currently not available");
        }

        // Rule 3
        Long residentCommunityId =
                resident.getFlat()
                    .getBlock()
                    .getCommunity()
                    .getCommunityId();

        Long amenityCommunityId =
                amenity.getCommunity().getCommunityId();

        if (!residentCommunityId.equals(amenityCommunityId)) {

            throw new IllegalArgumentException(
                    "Resident can only book amenities in their own community");
        }

        // Rule 4
        if (request.getBookingDate()
                .isBefore(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Past dates cannot be booked");
        }

        // Rule 5
        if (!request.getStartTime()
                .isBefore(request.getEndTime())) {

            throw new IllegalArgumentException(
                    "Start time must be before end time");
        }

        // Rule 6
        if (request.getStartTime()
                .isBefore(amenity.getOpeningTime())
            ||
            request.getEndTime()
                .isAfter(amenity.getClosingTime())) {

            throw new IllegalArgumentException(
                    "Booking must be within amenity operating hours");
        }

        // Rule 7
        long minutes =
                Duration.between(
                    request.getStartTime(),
                    request.getEndTime())
                .toMinutes();

        long maxMinutes =
                amenity.getMaxBookingHours() * 60L;

        if (minutes > maxMinutes) {

            throw new IllegalArgumentException(
                    "Booking exceeds maximum allowed duration of "
                    + amenity.getMaxBookingHours()
                    + " hours");
        }

        // Rule 8
        if (request.getNumberOfPeople()
                > amenity.getCapacity()) {

            throw new IllegalArgumentException(
                    "Number of people exceeds amenity capacity");
        }

        // Rule 9
        long overlaps =
                bookingRepository
                    .countOverlappingBookings(
                        amenity.getAmenityId(),
                        request.getBookingDate(),
                        request.getStartTime(),
                        request.getEndTime());

        if (overlaps > 0) {

            throw new IllegalArgumentException(
                    "Selected time slot is already booked");
        }

        AmenityBooking booking =
                new AmenityBooking();

        booking.setAmenity(amenity);
        booking.setResident(resident);

        booking.setBookingDate(
                request.getBookingDate());

        booking.setStartTime(
                request.getStartTime());

        booking.setEndTime(
                request.getEndTime());

        booking.setNumberOfPeople(
                request.getNumberOfPeople());

        booking.setPurpose(request.getPurpose());

        booking.setStatus(
                BookingStatus.CONFIRMED);

        return mapToResponse(
                bookingRepository.save(booking));
    }

    @Override
    public AmenityBookingResponse getBookingById(
            Long bookingId) {

        return mapToResponse(findBooking(bookingId));
    }

    @Override
    public List<AmenityBookingResponse> getAllBookings() {

        return mapList(bookingRepository.findAll());
    }

    @Override
    public List<AmenityBookingResponse> getByResident(
            Long residentId) {

        if (!residentRepository.existsById(residentId)) {

            throw new ResourceNotFoundException(
                    "Resident not found with id: "
                    + residentId);
        }

        return mapList(
                bookingRepository
                    .findByResidentResidentId(residentId));
    }

    @Override
    public List<AmenityBookingResponse> getByAmenity(
            Long amenityId) {

        if (!amenityRepository.existsById(amenityId)) {

            throw new ResourceNotFoundException(
                    "Amenity not found with id: "
                    + amenityId);
        }

        return mapList(
                bookingRepository
                    .findByAmenityAmenityId(amenityId));
    }

    @Override
    public List<AmenityBookingResponse> getByAmenityAndDate(
            Long amenityId,
            LocalDate date) {

        if (!amenityRepository.existsById(amenityId)) {

            throw new ResourceNotFoundException(
                    "Amenity not found with id: "
                    + amenityId);
        }

        return mapList(
                bookingRepository
                    .findByAmenityAmenityIdAndBookingDate(
                        amenityId,
                        date));
    }

    @Override
    public List<AmenityBookingResponse> getByCommunity(
            Long communityId) {

        if (!communityRepository.existsById(communityId)) {

            throw new ResourceNotFoundException(
                    "Community not found with id: "
                    + communityId);
        }

        return mapList(
                bookingRepository
                    .findByAmenityCommunityCommunityId(
                        communityId));
    }

    @Override
    public AmenityBookingResponse cancelBooking(
            Long bookingId,
            CancelBookingRequest request) {

        AmenityBooking booking =
                findBooking(bookingId);

        if (booking.getStatus()
                != BookingStatus.CONFIRMED) {

            throw new IllegalArgumentException(
                    "Only confirmed bookings can be cancelled");
        }

        booking.setStatus(
                BookingStatus.CANCELLED);

        booking.setCancelledAt(
                LocalDateTime.now());

        booking.setCancellationReason(
                request.getReason());

        return mapToResponse(
                bookingRepository.save(booking));
    }

    @Override
    public AmenityBookingResponse completeBooking(
            Long bookingId) {

        AmenityBooking booking =
                findBooking(bookingId);

        if (booking.getStatus()
                != BookingStatus.CONFIRMED) {

            throw new IllegalArgumentException(
                    "Only confirmed bookings can be completed");
        }

        booking.setStatus(
                BookingStatus.COMPLETED);

        return mapToResponse(
                bookingRepository.save(booking));
    }

    private AmenityBooking findBooking(Long id) {

        return bookingRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Booking not found with id: " + id));
    }

    private List<AmenityBookingResponse> mapList(
            List<AmenityBooking> bookings) {

        return bookings.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AmenityBookingResponse mapToResponse(
            AmenityBooking booking) {

        AmenityBookingResponse response =
                new AmenityBookingResponse();

        Amenity amenity = booking.getAmenity();
        Resident resident = booking.getResident();

        response.setBookingId(
                booking.getBookingId());

        response.setAmenityId(
                amenity.getAmenityId());

        response.setAmenityName(
                amenity.getName());

        response.setResidentId(
                resident.getResidentId());

        response.setResidentName(
                resident.getUser().getFirstName()
                + " "
                + resident.getUser().getLastName());

        response.setFlatId(
                resident.getFlat().getFlatId());

        response.setFlatNumber(
                resident.getFlat().getFlatNumber());

        response.setCommunityId(
                amenity.getCommunity().getCommunityId());

        response.setCommunityName(
                amenity.getCommunity().getName());

        response.setBookingDate(
                booking.getBookingDate());

        response.setStartTime(
                booking.getStartTime());

        response.setEndTime(
                booking.getEndTime());

        response.setNumberOfPeople(
                booking.getNumberOfPeople());

        response.setPurpose(
                booking.getPurpose());

        response.setStatus(
                booking.getStatus());

        return response;
    }
}