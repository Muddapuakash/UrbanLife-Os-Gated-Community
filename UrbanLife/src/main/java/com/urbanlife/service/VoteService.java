package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.VoteRequest;
import com.urbanlife.dto.VoteResponse;

public interface VoteService {

    VoteResponse castVote(
            Long pollId,
            Long userId,
            VoteRequest request);

    VoteResponse getUserVote(
            Long pollId,
            Long userId);

    List<VoteResponse> getUserVotes(
            Long userId);
}