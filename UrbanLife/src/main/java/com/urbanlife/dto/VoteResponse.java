package com.urbanlife.dto;

import java.time.LocalDateTime;

public class VoteResponse {

    private Long voteId;
    private Long pollId;
    private Long optionId;
    private String optionText;
    private Long userId;
    private LocalDateTime votedAt;

    public Long getVoteId() {
        return voteId;
    }

    public void setVoteId(Long voteId) {
        this.voteId = voteId;
    }

    public Long getPollId() {
        return pollId;
    }

    public void setPollId(Long pollId) {
        this.pollId = pollId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(
            String optionText) {
        this.optionText = optionText;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getVotedAt() {
        return votedAt;
    }

    public void setVotedAt(
            LocalDateTime votedAt) {
        this.votedAt = votedAt;
    }
}