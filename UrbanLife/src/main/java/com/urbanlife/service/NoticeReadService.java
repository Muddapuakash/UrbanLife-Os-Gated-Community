package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.NoticeReadResponse;

public interface NoticeReadService {

    NoticeReadResponse markAsRead(
            Long noticeId,
            Long residentId);

    List<NoticeReadResponse> getReaders(
            Long noticeId);

    List<NoticeReadResponse> getResidentReadHistory(
            Long residentId);

    long getReadCount(
            Long noticeId);
}