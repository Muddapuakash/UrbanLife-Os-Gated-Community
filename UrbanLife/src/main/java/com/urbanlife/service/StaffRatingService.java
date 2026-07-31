package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.StaffRatingRequest;
import com.urbanlife.dto.StaffRatingResponse;

public interface StaffRatingService {

    StaffRatingResponse addRating(
            Long staffId,
            StaffRatingRequest request);

    List<StaffRatingResponse> getStaffRatings(
            Long staffId);

    double getAverageRating(Long staffId);
}