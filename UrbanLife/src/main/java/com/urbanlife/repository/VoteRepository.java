package com.urbanlife.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.Vote;

public interface VoteRepository
        extends JpaRepository<Vote, Long> {

    boolean existsByPollPollIdAndUserUserId(
            Long pollId,
            Long userId);

    Optional<Vote>
        findByPollPollIdAndUserUserId(
            Long pollId,
            Long userId);

    long countByPollPollId(
            Long pollId);

    long countByOptionOptionId(
            Long optionId);

    List<Vote>
        findByUserUserIdOrderByVotedAtDesc(
            Long userId);
}