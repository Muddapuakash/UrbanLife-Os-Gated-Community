package com.urbanlife.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urbanlife.dto.VoteRequest;
import com.urbanlife.dto.VoteResponse;
import com.urbanlife.entity.Poll;
import com.urbanlife.entity.PollOption;
import com.urbanlife.entity.User;
import com.urbanlife.entity.Vote;
import com.urbanlife.enums.PollStatus;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.PollOptionRepository;
import com.urbanlife.repository.PollRepository;
import com.urbanlife.repository.UserRepository;
import com.urbanlife.repository.VoteRepository;
import com.urbanlife.service.VoteService;

@Service
@Transactional
public class VoteServiceImpl
        implements VoteService {

    private final VoteRepository voteRepository;
    private final PollRepository pollRepository;
    private final PollOptionRepository optionRepository;
    private final UserRepository userRepository;

    public VoteServiceImpl(
            VoteRepository voteRepository,
            PollRepository pollRepository,
            PollOptionRepository optionRepository,
            UserRepository userRepository) {

        this.voteRepository = voteRepository;
        this.pollRepository = pollRepository;
        this.optionRepository = optionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public VoteResponse castVote(
            Long pollId,
            Long userId,
            VoteRequest request) {

        Poll poll =
                findPoll(pollId);

        User user =
                findUser(userId);

        PollOption option =
                optionRepository
                    .findById(request.getOptionId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Poll option not found with id: "
                            + request.getOptionId()));

        if (poll.getStatus()
                != PollStatus.ACTIVE) {

            throw new IllegalStateException(
                "Voting is allowed only for active polls");
        }

        LocalDateTime now =
                LocalDateTime.now();

        if (now.isBefore(
                poll.getStartTime())) {

            throw new IllegalStateException(
                "Voting has not started yet");
        }

        if (!now.isBefore(
                poll.getEndTime())) {

            throw new IllegalStateException(
                "Voting period has ended");
        }

        /*
         * Very important:
         * selected option must belong to
         * requested poll.
         */
        if (!option.getPoll()
                .getPollId()
                .equals(pollId)) {

            throw new IllegalArgumentException(
                "Selected option does not belong to this poll");
        }

        if (voteRepository
                .existsByPollPollIdAndUserUserId(
                    pollId,
                    userId)) {

            throw new IllegalStateException(
                "User has already voted in this poll");
        }

        Vote vote =
                new Vote();

        vote.setPoll(poll);
        vote.setOption(option);
        vote.setUser(user);

        return mapToResponse(
                voteRepository.save(vote));
    }

    @Override
    @Transactional(readOnly = true)
    public VoteResponse getUserVote(
            Long pollId,
            Long userId) {

        Vote vote =
                voteRepository
                    .findByPollPollIdAndUserUserId(
                        pollId,
                        userId)
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Vote not found"));

        return mapToResponse(vote);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoteResponse>
            getUserVotes(
                Long userId) {

        findUser(userId);

        return voteRepository
                .findByUserUserIdOrderByVotedAtDesc(
                    userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private Poll findPoll(
            Long pollId) {

        return pollRepository
                .findById(pollId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Poll not found with id: "
                        + pollId));
    }

    private User findUser(
            Long userId) {

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "User not found with id: "
                        + userId));
    }

    private VoteResponse mapToResponse(
            Vote vote) {

        VoteResponse response =
                new VoteResponse();

        response.setVoteId(
                vote.getVoteId());

        response.setPollId(
                vote.getPoll()
                    .getPollId());

        response.setOptionId(
                vote.getOption()
                    .getOptionId());

        response.setOptionText(
                vote.getOption()
                    .getOptionText());

        response.setUserId(
                vote.getUser()
                    .getUserId());

        response.setVotedAt(
                vote.getVotedAt());

        return response;
    }
}