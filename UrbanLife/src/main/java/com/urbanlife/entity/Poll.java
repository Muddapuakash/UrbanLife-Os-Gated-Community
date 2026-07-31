package com.urbanlife.entity;

import java.time.LocalDateTime;

import com.urbanlife.enums.PollStatus;

import jakarta.persistence.*;

@Entity
@Table(
    name = "polls",
    indexes = {
        @Index(
            name = "idx_poll_community",
            columnList = "community_id"
        ),
        @Index(
            name = "idx_poll_status",
            columnList = "status"
        )
    }
)
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pollId;

    @Column(nullable = false, length = 200)
    private String question;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PollStatus status;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "community_id",
        nullable = false
    )
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "created_by",
        nullable = false
    )
    private User createdBy;

    @Column(
        nullable = false,
        updatable = false
    )
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Poll() {
    }

    @PrePersist
    public void onCreate() {

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (status == null) {
            status = PollStatus.DRAFT;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getPollId() {
        return pollId;
    }

    public void setPollId(Long pollId) {
        this.pollId = pollId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
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

    public void setStatus(PollStatus status) {
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

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(
            Community community) {
        this.community = community;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(
            User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}