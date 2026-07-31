package com.urbanlife.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.urbanlife.enums.PollStatus;

public class PollResponse {

    private Long pollId;

    private String question;
    private String description;

    private PollStatus status;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Long communityId;
    private Long createdByUserId;

    private Long totalVotes;

    private List<PollOptionResponse> options;

    private LocalDateTime createdAt;

    public Long getPollId() {
        return pollId;
    }

    public void setPollId(Long pollId) {
        this.pollId = pollId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(
            String question) {
        this.question = question;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {
        this.description = description;
    }

    public PollStatus getStatus() {
        return status;
    }

    public void setStatus(
            PollStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(
            LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(
            LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(
            Long communityId) {
        this.communityId = communityId;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(
            Long createdByUserId) {
        this.createdByUserId =
                createdByUserId;
    }

    public Long getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(
            Long totalVotes) {
        this.totalVotes = totalVotes;
    }

    public List<PollOptionResponse> getOptions() {
        return options;
    }

    public void setOptions(
            List<PollOptionResponse> options) {
        this.options = options;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}