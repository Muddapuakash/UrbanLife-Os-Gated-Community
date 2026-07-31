package com.urbanlife.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.*;

public class PollRequest {

    @NotBlank
    @Size(max = 200)
    private String question;

    @Size(max = 1000)
    private String description;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    private Long communityId;

    @NotNull
    private Long createdByUserId;

    @NotNull
    @Size(min = 2, max = 10)
    private List<
            @NotBlank
            @Size(max = 200)
            String> options;

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

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(
            List<String> options) {
        this.options = options;
    }
}