package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.EmergencyUpdate;

public interface EmergencyUpdateRepository
        extends JpaRepository<EmergencyUpdate, Long> {

    List<EmergencyUpdate>
        findByEmergencyEmergencyIdOrderByCreatedAtAsc(
            Long emergencyId);
}