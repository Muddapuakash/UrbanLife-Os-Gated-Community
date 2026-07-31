package com.urbanlife.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.Visitor;
import com.urbanlife.enums.VisitStatus;
import com.urbanlife.enums.VisitorType;

public interface VisitorRepository
        extends JpaRepository<Visitor, Long> {
	long countByResidentFlatBlockCommunityCommunityId(Long communityId);
    Optional<Visitor> findByPassCode(String passCode);

    boolean existsByPassCode(String passCode);

    List<Visitor> findByResidentResidentId(Long residentId);

    List<Visitor> findByStatus(VisitStatus status);

    List<Visitor> findByVisitorType(VisitorType visitorType);

    List<Visitor>
        findByResidentFlatBlockCommunityCommunityId(
            Long communityId);

    List<Visitor> findByResidentFlatFlatId(Long flatId);
}