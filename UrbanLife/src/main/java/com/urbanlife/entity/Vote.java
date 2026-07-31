package com.urbanlife.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(
    name = "votes",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_poll_user_vote",
            columnNames = {
                "poll_id",
                "user_id"
            }
        )
    },
    indexes = {
        @Index(
            name = "idx_vote_poll",
            columnList = "poll_id"
        ),
        @Index(
            name = "idx_vote_option",
            columnList = "option_id"
        )
    }
)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "poll_id",
        nullable = false
    )
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "option_id",
        nullable = false
    )
    private PollOption option;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false
    )
    private User user;

    @Column(
        nullable = false,
        updatable = false
    )
    private LocalDateTime votedAt;

    public Vote() {
    }

    @PrePersist
    public void onCreate() {
        votedAt = LocalDateTime.now();
    }

    public Long getVoteId() {
        return voteId;
    }

    public void setVoteId(Long voteId) {
        this.voteId = voteId;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public PollOption getOption() {
        return option;
    }

    public void setOption(
            PollOption option) {
        this.option = option;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getVotedAt() {
        return votedAt;
    }
}