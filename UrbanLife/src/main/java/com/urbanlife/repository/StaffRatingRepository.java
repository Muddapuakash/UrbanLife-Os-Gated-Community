package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.StaffRating;

public interface StaffRatingRepository
        extends JpaRepository<StaffRating, Long> {

    List<StaffRating>
        findByStaffStaffId(Long staffId);
}