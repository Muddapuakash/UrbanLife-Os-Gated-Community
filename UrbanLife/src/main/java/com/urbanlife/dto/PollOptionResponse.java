package com.urbanlife.dto;

public class PollOptionResponse {

    private Long optionId;
    private String optionText;
    private Long voteCount;
    private Double percentage;

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(
            Long optionId) {
        this.optionId = optionId;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(
            String optionText) {
        this.optionText = optionText;
    }

    public Long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(
            Long voteCount) {
        this.voteCount = voteCount;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(
            Double percentage) {
        this.percentage = percentage;
    }
}