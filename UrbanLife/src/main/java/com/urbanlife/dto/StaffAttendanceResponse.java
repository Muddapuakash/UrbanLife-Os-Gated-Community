package com.urbanlife.dto;

import java.time.LocalDateTime;

import com.urbanlife.enums.AttendanceStatus;

public class StaffAttendanceResponse {

    private Long attendanceId;

    private Long staffId;
    private String staffName;

    private Long communityId;

    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    private AttendanceStatus status;

    private Long entryRecordedById;
    private String entryRecordedByName;

    private Long exitRecordedById;
    private String exitRecordedByName;

    public StaffAttendanceResponse() {
    }

    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
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

    public Long getEntryRecordedById() {
        return entryRecordedById;
    }

    public void setEntryRecordedById(Long entryRecordedById) {
        this.entryRecordedById = entryRecordedById;
    }

    public String getEntryRecordedByName() {
        return entryRecordedByName;
    }

    public void setEntryRecordedByName(String entryRecordedByName) {
        this.entryRecordedByName = entryRecordedByName;
    }

    public Long getExitRecordedById() {
        return exitRecordedById;
    }

    public void setExitRecordedById(Long exitRecordedById) {
        this.exitRecordedById = exitRecordedById;
    }

    public String getExitRecordedByName() {
        return exitRecordedByName;
    }

    public void setExitRecordedByName(String exitRecordedByName) {
        this.exitRecordedByName = exitRecordedByName;
    }
}