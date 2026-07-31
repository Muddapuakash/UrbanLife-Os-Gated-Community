package com.urbanlife.dto;

import java.time.LocalDateTime;

public class NoticeReadResponse {

    private Long noticeReadId;

    private Long noticeId;
    private String noticeTitle;

    private Long residentId;
    private String residentName;

    private LocalDateTime readAt;

    public Long getNoticeReadId() {
        return noticeReadId;
    }

    public void setNoticeReadId(Long noticeReadId) {
        this.noticeReadId = noticeReadId;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public String getResidentName() {
        return residentName;
    }

    public void setResidentName(String residentName) {
        this.residentName = residentName;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}