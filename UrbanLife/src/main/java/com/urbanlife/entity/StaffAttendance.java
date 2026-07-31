package com.urbanlife.entity;

import java.time.LocalDateTime;

import com.urbanlife.enums.AttendanceStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "staff_attendance")
public class StaffAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private DomesticStaff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_recorded_by")
    private User entryRecordedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exit_recorded_by")
    private User exitRecordedBy;

    public StaffAttendance() {
    }

    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public DomesticStaff getStaff() {
        return staff;
    }

    public void setStaff(DomesticStaff staff) {
        this.staff = staff;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public User getEntryRecordedBy() {
        return entryRecordedBy;
    }

    public void setEntryRecordedBy(User entryRecordedBy) {
        this.entryRecordedBy = entryRecordedBy;
    }

    public User getExitRecordedBy() {
        return exitRecordedBy;
    }

    public void setExitRecordedBy(User exitRecordedBy) {
        this.exitRecordedBy = exitRecordedBy;
    }
}