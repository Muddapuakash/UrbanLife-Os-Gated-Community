package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.StaffFlatAssignment;

public interface StaffFlatAssignmentRepository
        extends JpaRepository<StaffFlatAssignment, Long> {

    List<StaffFlatAssignment>
        findByStaffStaffId(Long staffId);

    List<StaffFlatAssignment>
        findByStaffStaffIdAndActiveTrue(Long staffId);

    List<StaffFlatAssignment>
        findByFlatFlatIdAndActiveTrue(Long flatId);

    List<StaffFlatAssignment>
        findByResidentResidentIdAndActiveTrue(
            Long residentId);

    boolean existsByStaffStaffIdAndFlatFlatIdAndActiveTrue(
            Long staffId,
            Long flatId);
}