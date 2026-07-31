package com.urbanlife.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.urbanlife.dto.StaffRatingRequest;
import com.urbanlife.dto.StaffRatingResponse;
import com.urbanlife.entity.DomesticStaff;
import com.urbanlife.entity.Resident;
import com.urbanlife.entity.StaffRating;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.DomesticStaffRepository;
import com.urbanlife.repository.ResidentRepository;
import com.urbanlife.repository.StaffRatingRepository;
import com.urbanlife.service.StaffRatingService;

@Service
public class StaffRatingServiceImpl
        implements StaffRatingService {

    private final StaffRatingRepository ratingRepository;
    private final DomesticStaffRepository staffRepository;
    private final ResidentRepository residentRepository;

    public StaffRatingServiceImpl(
            StaffRatingRepository ratingRepository,
            DomesticStaffRepository staffRepository,
            ResidentRepository residentRepository) {

        this.ratingRepository = ratingRepository;
        this.staffRepository = staffRepository;
        this.residentRepository = residentRepository;
    }

    @Override
    public StaffRatingResponse addRating(
            Long staffId,
            StaffRatingRequest request) {

        DomesticStaff staff =
            staffRepository.findById(staffId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Staff not found with id: "
                        + staffId));

        Resident resident =
            residentRepository
                .findById(request.getResidentId())
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Resident not found with id: "
                        + request.getResidentId()));

        if (resident.getFlat() == null) {
            throw new IllegalArgumentException(
                "Resident is not assigned to a flat");
        }

        if (!resident.getFlat()
                .getBlock()
                .getCommunity()
                .getCommunityId()
                .equals(
                    staff.getCommunity()
                        .getCommunityId())) {

            throw new IllegalArgumentException(
                "Resident and staff belong to different communities");
        }

        StaffRating rating = new StaffRating();

        rating.setStaff(staff);
        rating.setResident(resident);
        rating.setRating(request.getRating());
        rating.setReview(request.getReview());

        return map(
            ratingRepository.save(rating));
    }

    @Override
    public List<StaffRatingResponse>
            getStaffRatings(Long staffId) {

        if (!staffRepository.existsById(staffId)) {
            throw new ResourceNotFoundException(
                "Staff not found with id: " + staffId);
        }

        return ratingRepository
            .findByStaffStaffId(staffId)
            .stream()
            .map(this::map)
            .toList();
    }

    @Override
    public double getAverageRating(
            Long staffId) {

        if (!staffRepository.existsById(staffId)) {
            throw new ResourceNotFoundException(
                "Staff not found with id: " + staffId);
        }

        return ratingRepository
            .findByStaffStaffId(staffId)
            .stream()
            .mapToInt(StaffRating::getRating)
            .average()
            .orElse(0.0);
    }

    private StaffRatingResponse map(
            StaffRating rating) {

        StaffRatingResponse response =
            new StaffRatingResponse();

        response.setRatingId(
            rating.getRatingId());

        response.setStaffId(
            rating.getStaff().getStaffId());

        response.setStaffName(
            rating.getStaff().getName());

        response.setResidentId(
            rating.getResident().getResidentId());

        response.setResidentName(
            rating.getResident()
                .getUser().getFirstName()
            + " "
            + rating.getResident()
                .getUser().getLastName());

        response.setRating(
            rating.getRating());

        response.setReview(
            rating.getReview());

        response.setCreatedAt(
            rating.getCreatedAt());

        return response;
    }
}