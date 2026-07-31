package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.NoticeRead;

public interface NoticeReadRepository
        extends JpaRepository<NoticeRead, Long> {

    boolean existsByNoticeNoticeIdAndResidentResidentId(
            Long noticeId,
            Long residentId);

    List<NoticeRead>
        findByNoticeNoticeId(Long noticeId);

    List<NoticeRead>
        findByResidentResidentId(Long residentId);

    long countByNoticeNoticeId(Long noticeId);
}