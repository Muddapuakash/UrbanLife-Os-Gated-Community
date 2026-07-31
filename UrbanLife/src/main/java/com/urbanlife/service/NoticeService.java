package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.CreateNoticeRequest;
import com.urbanlife.dto.NoticeResponse;
import com.urbanlife.enums.NoticePriority;
import com.urbanlife.enums.NoticeType;

public interface NoticeService {

    NoticeResponse createNotice(
            CreateNoticeRequest request);

    NoticeResponse getNoticeById(
            Long noticeId);

    List<NoticeResponse> getAllNotices();

    List<NoticeResponse> getCommunityNotices(
            Long communityId);

    List<NoticeResponse> getPublishedCommunityNotices(
            Long communityId);

    List<NoticeResponse> getNoticesByType(
            Long communityId,
            NoticeType type);

    List<NoticeResponse> getNoticesByPriority(
            Long communityId,
            NoticePriority priority);

    NoticeResponse publishNotice(
            Long noticeId);

    NoticeResponse cancelNotice(
            Long noticeId);

    int expireNotices();
}