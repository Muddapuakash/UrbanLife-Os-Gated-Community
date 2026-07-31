package com.urbanlife.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.EventRegistration;
import com.urbanlife.enums.RegistrationStatus;

public interface EventRegistrationRepository
        extends JpaRepository<EventRegistration, Long> {

    Optional<EventRegistration>
        findByEventEventIdAndUserUserId(
            Long eventId,
            Long userId);

    boolean existsByEventEventIdAndUserUserId(
            Long eventId,
            Long userId);

    long countByEventEventIdAndStatus(
            Long eventId,
            RegistrationStatus status);

    List<EventRegistration>
        findByEventEventIdOrderByRegisteredAtAsc(
            Long eventId);

    List<EventRegistration>
        findByUserUserIdOrderByRegisteredAtDesc(
            Long userId);
}