package com.urbanlife.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "poll_options",
    indexes = {
        @Index(
            name = "idx_option_poll",
            columnList = "poll_id"
        )
    }
)
public class PollOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionId;

    @Column(nullable = false, length = 200)
    private String optionText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "poll_id",
        nullable = false
    )
    private Poll poll;

    public PollOption() {
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

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }
}