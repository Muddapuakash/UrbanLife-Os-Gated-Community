package com.urbanlife.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(
    name = "notice_reads",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {
                "notice_id",
                "resident_id"
            }
        )
    }
)
public class NoticeRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeReadId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @Column(nullable = false)
    private LocalDateTime readAt;

    public NoticeRead() {
    }

    @PrePersist
    public void onCreate() {

        if (readAt == null) {
            readAt = LocalDateTime.now();
        }
    }

    public Long getNoticeReadId() {
        return noticeReadId;
    }

    public void setNoticeReadId(Long noticeReadId) {
        this.noticeReadId = noticeReadId;
    }

    public Notice getNotice() {
        return notice;
    }

    public void setNotice(Notice notice) {
        this.notice = notice;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}