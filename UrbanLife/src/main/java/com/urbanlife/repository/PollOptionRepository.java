package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.PollOption;

public interface PollOptionRepository
        extends JpaRepository<PollOption, Long> {

    List<PollOption>
        findByPollPollIdOrderByOptionIdAsc(
            Long pollId);

    long countByPollPollId(
            Long pollId);
}