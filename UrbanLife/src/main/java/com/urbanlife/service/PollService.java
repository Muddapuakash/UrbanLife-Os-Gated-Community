package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.PollRequest;
import com.urbanlife.dto.PollResponse;
import com.urbanlife.enums.PollStatus;

public interface PollService {

    PollResponse createPoll(
            PollRequest request);

    PollResponse getPoll(
            Long pollId);

    List<PollResponse> getCommunityPolls(
            Long communityId);

    List<PollResponse> getPollsByStatus(
            Long communityId,
            PollStatus status);

    List<PollResponse> getCreatedPolls(
            Long userId);

    PollResponse updatePoll(
            Long pollId,
            PollRequest request);

    PollResponse activatePoll(
            Long pollId);

    PollResponse closePoll(
            Long pollId);

    PollResponse cancelPoll(
            Long pollId);

    void deletePoll(
            Long pollId);
}