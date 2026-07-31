package com.urbanlife.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.urbanlife.dto.NoticeReadResponse;
import com.urbanlife.entity.Notice;
import com.urbanlife.entity.NoticeRead;
import com.urbanlife.entity.Resident;
import com.urbanlife.enums.NoticeStatus;
import com.urbanlife.enums.NoticeTargetType;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.NoticeReadRepository;
import com.urbanlife.repository.NoticeRepository;
import com.urbanlife.repository.ResidentRepository;
import com.urbanlife.service.NoticeReadService;

@Service
public class NoticeReadServiceImpl
        implements NoticeReadService {

    private final NoticeReadRepository readRepository;
    private final NoticeRepository noticeRepository;
    private final ResidentRepository residentRepository;

    public NoticeReadServiceImpl(
            NoticeReadRepository readRepository,
            NoticeRepository noticeRepository,
            ResidentRepository residentRepository) {

        this.readRepository = readRepository;
        this.noticeRepository = noticeRepository;
        this.residentRepository = residentRepository;
    }

    @Override
    public NoticeReadResponse markAsRead(
            Long noticeId,
            Long residentId) {

        Notice notice =
                noticeRepository
                    .findById(noticeId)
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Notice not found with id: "
                            + noticeId));

        Resident resident =
                residentRepository
                    .findById(residentId)
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Resident not found with id: "
                            + residentId));

        if (notice.getStatus()
                != NoticeStatus.PUBLISHED) {

            throw new IllegalArgumentException(
                "Only published notices can be read");
        }

        Long noticeCommunityId =
                notice.getCommunity()
                    .getCommunityId();

        Long residentCommunityId =
                resident.getFlat()
                    .getBlock()
                    .getCommunity()
                    .getCommunityId();

        if (!noticeCommunityId
                .equals(residentCommunityId)) {

            throw new IllegalArgumentException(
                "Resident does not belong to notice community");
        }

        if (notice.getTargetType()
                == NoticeTargetType.BLOCK) {

            Long targetBlockId =
                    notice.getBlock()
                        .getBlockId();

            Long residentBlockId =
                    resident.getFlat()
                        .getBlock()
                        .getBlockId();

            if (!targetBlockId
                    .equals(residentBlockId)) {

                throw new IllegalArgumentException(
                    "Notice is not intended for resident's block");
            }
        }

        /*
         * If already read, return existing record.
         */
        if (readRepository
                .existsByNoticeNoticeIdAndResidentResidentId(
                    noticeId,
                    residentId)) {

            return readRepository
                    .findByNoticeNoticeId(noticeId)
                    .stream()
                    .filter(r ->
                        r.getResident()
                         .getResidentId()
                         .equals(residentId))
                    .findFirst()
                    .map(this::mapToResponse)
                    .orElseThrow();
        }

        NoticeRead noticeRead =
                new NoticeRead();

        noticeRead.setNotice(notice);
        noticeRead.setResident(resident);

        return mapToResponse(
                readRepository.save(noticeRead));
    }

    @Override
    public List<NoticeReadResponse> getReaders(
            Long noticeId) {

        if (!noticeRepository.existsById(noticeId)) {

            throw new ResourceNotFoundException(
                "Notice not found with id: "
                + noticeId);
        }

        return readRepository
                .findByNoticeNoticeId(noticeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<NoticeReadResponse>
            getResidentReadHistory(
                    Long residentId) {

        if (!residentRepository.existsById(residentId)) {

            throw new ResourceNotFoundException(
                "Resident not found with id: "
                + residentId);
        }

        return readRepository
                .findByResidentResidentId(residentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public long getReadCount(
            Long noticeId) {

        if (!noticeRepository.existsById(noticeId)) {

            throw new ResourceNotFoundException(
                "Notice not found with id: "
                + noticeId);
        }

        return readRepository
                .countByNoticeNoticeId(noticeId);
    }

    private NoticeReadResponse mapToResponse(
            NoticeRead read) {

        NoticeReadResponse response =
                new NoticeReadResponse();

        response.setNoticeReadId(
                read.getNoticeReadId());

        response.setNoticeId(
                read.getNotice()
                    .getNoticeId());

        response.setNoticeTitle(
                read.getNotice()
                    .getTitle());

        response.setResidentId(
                read.getResident()
                    .getResidentId());

        response.setResidentName(
                read.getResident()
                    .getUser()
                    .getFirstName()
                + " "
                + read.getResident()
                    .getUser()
                    .getLastName());

        response.setReadAt(
                read.getReadAt());

        return response;
    }
}