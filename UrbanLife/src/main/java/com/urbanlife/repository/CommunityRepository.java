package com.urbanlife.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.Community;
import com.urbanlife.enums.CommunityStatus;

public interface CommunityRepository
        extends JpaRepository<Community, Long> {

    boolean existsByName(String name);

    boolean existsByRegistrationNumber(String registrationNumber);

    Optional<Community> findByRegistrationNumber(String registrationNumber);

    List<Community> findByCity(String city);

    List<Community> findByStatus(CommunityStatus status);
}