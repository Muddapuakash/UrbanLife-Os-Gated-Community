package com.urbanlife.serviceimpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urbanlife.dto.PollOptionResponse;
import com.urbanlife.dto.PollRequest;
import com.urbanlife.dto.PollResponse;
import com.urbanlife.entity.Community;
import com.urbanlife.entity.Poll;
import com.urbanlife.entity.PollOption;
import com.urbanlife.entity.User;
import com.urbanlife.enums.PollStatus;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.repository.PollOptionRepository;
import com.urbanlife.repository.PollRepository;
import com.urbanlife.repository.UserRepository;
import com.urbanlife.repository.VoteRepository;
import com.urbanlife.service.PollService;

@Service
@Transactional
public class PollServiceImpl
        implements PollService {

    private final PollRepository pollRepository;
    private final PollOptionRepository optionRepository;
    private final VoteRepository voteRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    public PollServiceImpl(
            PollRepository pollRepository,
            PollOptionRepository optionRepository,
            VoteRepository voteRepository,
            CommunityRepository communityRepository,
            UserRepository userRepository) {

        this.pollRepository = pollRepository;
        this.optionRepository = optionRepository;
        this.voteRepository = voteRepository;
        this.communityRepository = communityRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PollResponse createPoll(
            PollRequest request) {

        validateDates(request);

        validateOptions(request.getOptions());

        Community community =
                findCommunity(
                    request.getCommunityId());

        User creator =
                findUser(
                    request.getCreatedByUserId());

        Poll poll = new Poll();

        mapRequest(poll, request);

        poll.setCommunity(community);
        poll.setCreatedBy(creator);
        poll.setStatus(PollStatus.DRAFT);

        Poll savedPoll =
                pollRepository.save(poll);

        for (String optionText :
                request.getOptions()) {

            PollOption option =
                    new PollOption();

            option.setOptionText(
                    optionText.trim());

            option.setPoll(savedPoll);

            optionRepository.save(option);
        }

        return mapToResponse(savedPoll);
    }

    @Override
    @Transactional(readOnly = true)
    public PollResponse getPoll(
            Long pollId) {

        return mapToResponse(
                findPoll(pollId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PollResponse>
            getCommunityPolls(
                Long communityId) {

        findCommunity(communityId);

        return pollRepository
                .findByCommunityCommunityIdOrderByCreatedAtDesc(
                    communityId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PollResponse>
            getPollsByStatus(
                Long communityId,
                PollStatus status) {

        findCommunity(communityId);

        return pollRepository
                .findByCommunityCommunityIdAndStatusOrderByEndTimeAsc(
                    communityId,
                    status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PollResponse>
            getCreatedPolls(
                Long userId) {

        findUser(userId);

        return pollRepository
                .findByCreatedByUserIdOrderByCreatedAtDesc(
                    userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PollResponse updatePoll(
            Long pollId,
            PollRequest request) {

        validateDates(request);
        validateOptions(request.getOptions());

        Poll poll =
                findPoll(pollId);

        if (poll.getStatus()
                != PollStatus.DRAFT) {

            throw new IllegalStateException(
                "Only draft polls can be updated");
        }

        Community community =
                findCommunity(
                    request.getCommunityId());

        User creator =
                findUser(
                    request.getCreatedByUserId());

        mapRequest(poll, request);

        poll.setCommunity(community);
        poll.setCreatedBy(creator);

        /*
         * Since DRAFT polls cannot have votes,
         * replacing options is safe.
         */
        List<PollOption> existingOptions =
                optionRepository
                    .findByPollPollIdOrderByOptionIdAsc(
                        pollId);

        optionRepository
                .deleteAll(existingOptions);

        for (String optionText :
                request.getOptions()) {

            PollOption option =
                    new PollOption();

            option.setPoll(poll);

            option.setOptionText(
                    optionText.trim());

            optionRepository.save(option);
        }

        return mapToResponse(
                pollRepository.save(poll));
    }

    @Override
    public PollResponse activatePoll(
            Long pollId) {

        Poll poll =
                findPoll(pollId);

        if (poll.getStatus()
                != PollStatus.DRAFT) {

            throw new IllegalStateException(
                "Only draft polls can be activated");
        }

        if (poll.getEndTime()
                .isBefore(LocalDateTime.now())
                || poll.getEndTime()
                    .isEqual(LocalDateTime.now())) {

            throw new IllegalStateException(
                "Poll end time has already passed");
        }

        long options =
                optionRepository
                    .countByPollPollId(pollId);

        if (options < 2) {

            throw new IllegalStateException(
                "Poll must have at least two options");
        }

        poll.setStatus(
                PollStatus.ACTIVE);

        return mapToResponse(
                pollRepository.save(poll));
    }

    @Override
    public PollResponse closePoll(
            Long pollId) {

        Poll poll =
                findPoll(pollId);

        if (poll.getStatus()
                != PollStatus.ACTIVE) {

            throw new IllegalStateException(
                "Only active polls can be closed");
        }

        poll.setStatus(
                PollStatus.CLOSED);

        return mapToResponse(
                pollRepository.save(poll));
    }

    @Override
    public PollResponse cancelPoll(
            Long pollId) {

        Poll poll =
                findPoll(pollId);

        if (poll.getStatus()
                == PollStatus.CLOSED) {

            throw new IllegalStateException(
                "Closed poll cannot be cancelled");
        }

        if (poll.getStatus()
                == PollStatus.CANCELLED) {

            throw new IllegalStateException(
                "Poll is already cancelled");
        }

        poll.setStatus(
                PollStatus.CANCELLED);

        return mapToResponse(
                pollRepository.save(poll));
    }

    @Override
    public void deletePoll(
            Long pollId) {

        Poll poll =
                findPoll(pollId);

        if (poll.getStatus()
                != PollStatus.DRAFT) {

            throw new IllegalStateException(
                "Only draft polls can be deleted");
        }

        List<PollOption> options =
                optionRepository
                    .findByPollPollIdOrderByOptionIdAsc(
                        pollId);

        optionRepository.deleteAll(options);

        pollRepository.delete(poll);
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

    private Community findCommunity(
            Long communityId) {

        return communityRepository
                .findById(communityId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Community not found with id: "
                        + communityId));
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

    private void validateDates(
            PollRequest request) {

        if (!request.getEndTime()
                .isAfter(
                    request.getStartTime())) {

            throw new IllegalArgumentException(
                "Poll end time must be after start time");
        }
    }

    private void validateOptions(
            List<String> options) {

        long uniqueCount =
                options.stream()
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .distinct()
                    .count();

        if (uniqueCount
                != options.size()) {

            throw new IllegalArgumentException(
                "Duplicate poll options are not allowed");
        }
    }

    private void mapRequest(
            Poll poll,
            PollRequest request) {

        poll.setQuestion(
                request.getQuestion());

        poll.setDescription(
                request.getDescription());

        poll.setStartTime(
                request.getStartTime());

        poll.setEndTime(
                request.getEndTime());
    }

    private PollResponse mapToResponse(
            Poll poll) {

        PollResponse response =
                new PollResponse();

        response.setPollId(
                poll.getPollId());

        response.setQuestion(
                poll.getQuestion());

        response.setDescription(
                poll.getDescription());

        response.setStatus(
                poll.getStatus());

        response.setStartTime(
                poll.getStartTime());

        response.setEndTime(
                poll.getEndTime());

        response.setCommunityId(
                poll.getCommunity()
                    .getCommunityId());

        response.setCreatedByUserId(
                poll.getCreatedBy()
                    .getUserId());

        response.setCreatedAt(
                poll.getCreatedAt());

        long totalVotes =
                voteRepository
                    .countByPollPollId(
                        poll.getPollId());

        response.setTotalVotes(totalVotes);

        List<PollOption> options =
                optionRepository
                    .findByPollPollIdOrderByOptionIdAsc(
                        poll.getPollId());

        List<PollOptionResponse>
                optionResponses =
                    new ArrayList<>();

        for (PollOption option : options) {

            long votes =
                    voteRepository
                        .countByOptionOptionId(
                            option.getOptionId());

            double percentage =
                    totalVotes == 0
                    ? 0.0
                    : ((double) votes
                        / totalVotes) * 100;

            PollOptionResponse optionResponse =
                    new PollOptionResponse();

            optionResponse.setOptionId(
                    option.getOptionId());

            optionResponse.setOptionText(
                    option.getOptionText());

            optionResponse.setVoteCount(votes);

            optionResponse.setPercentage(
                    Math.round(
                        percentage * 100.0
                    ) / 100.0);

            optionResponses.add(
                    optionResponse);
        }

        response.setOptions(
                optionResponses);

        return response;
    }
}