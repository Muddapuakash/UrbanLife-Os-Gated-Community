package com.urbanlife.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.urbanlife.dto.CreateNoticeRequest;
import com.urbanlife.dto.NoticeResponse;
import com.urbanlife.entity.Block;
import com.urbanlife.entity.Community;
import com.urbanlife.entity.Notice;
import com.urbanlife.enums.NoticePriority;
import com.urbanlife.enums.NoticeStatus;
import com.urbanlife.enums.NoticeTargetType;
import com.urbanlife.enums.NoticeType;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.BlockRepository;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.repository.NoticeRepository;
import com.urbanlife.service.NoticeService;

@Service
public class NoticeServiceImpl
        implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final CommunityRepository communityRepository;
    private final BlockRepository blockRepository;

    public NoticeServiceImpl(
            NoticeRepository noticeRepository,
            CommunityRepository communityRepository,
            BlockRepository blockRepository) {

        this.noticeRepository = noticeRepository;
        this.communityRepository = communityRepository;
        this.blockRepository = blockRepository;
    }

    @Override
    public NoticeResponse createNotice(
            CreateNoticeRequest request) {

        Community community =
                communityRepository
                    .findById(request.getCommunityId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Community not found with id: "
                            + request.getCommunityId()));

        Block block = null;

        /*
         * BLOCK target requires blockId.
         */
        if (request.getTargetType()
                == NoticeTargetType.BLOCK) {

            if (request.getBlockId() == null) {

                throw new IllegalArgumentException(
                    "Block id is required for BLOCK target");
            }

            block =
                blockRepository
                    .findById(request.getBlockId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Block not found with id: "
                            + request.getBlockId()));

            /*
             * Prevent sending a notice to a block
             * belonging to another community.
             */
            if (!block.getCommunity()
                    .getCommunityId()
                    .equals(community.getCommunityId())) {

                throw new IllegalArgumentException(
                    "Block does not belong to selected community");
            }
        }

        if (request.getTargetType()
                == NoticeTargetType.COMMUNITY
                && request.getBlockId() != null) {

            throw new IllegalArgumentException(
                "Block id must not be provided for COMMUNITY target");
        }

        if (request.getExpiresAt() != null
                && request.getExpiresAt()
                    .isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                "Expiry date must be in the future");
        }

        Notice notice = new Notice();

        notice.setTitle(request.getTitle());
        notice.setMessage(request.getMessage());

        notice.setNoticeType(
                request.getNoticeType());

        notice.setPriority(
                request.getPriority());

        notice.setTargetType(
                request.getTargetType());

        notice.setCommunity(community);
        notice.setBlock(block);

        notice.setExpiresAt(
                request.getExpiresAt());

        notice.setStatus(
                NoticeStatus.DRAFT);

        return mapToResponse(
                noticeRepository.save(notice));
    }

    @Override
    public NoticeResponse getNoticeById(
            Long noticeId) {

        return mapToResponse(
                findNotice(noticeId));
    }

    @Override
    public List<NoticeResponse> getAllNotices() {

        return mapList(
                noticeRepository.findAll());
    }

    @Override
    public List<NoticeResponse> getCommunityNotices(
            Long communityId) {

        validateCommunity(communityId);

        return mapList(
                noticeRepository
                    .findByCommunityCommunityId(
                        communityId));
    }

    @Override
    public List<NoticeResponse>
            getPublishedCommunityNotices(
                    Long communityId) {

        validateCommunity(communityId);

        return mapList(
                noticeRepository
                    .findByCommunityCommunityIdAndStatus(
                        communityId,
                        NoticeStatus.PUBLISHED));
    }

    @Override
    public List<NoticeResponse> getNoticesByType(
            Long communityId,
            NoticeType type) {

        validateCommunity(communityId);

        return mapList(
                noticeRepository
                    .findByCommunityCommunityIdAndNoticeType(
                        communityId,
                        type));
    }

    @Override
    public List<NoticeResponse> getNoticesByPriority(
            Long communityId,
            NoticePriority priority) {

        validateCommunity(communityId);

        return mapList(
                noticeRepository
                    .findByCommunityCommunityIdAndPriority(
                        communityId,
                        priority));
    }

    @Override
    public NoticeResponse publishNotice(
            Long noticeId) {

        Notice notice = findNotice(noticeId);

        if (notice.getStatus()
                != NoticeStatus.DRAFT) {

            throw new IllegalArgumentException(
                "Only draft notices can be published");
        }

        if (notice.getExpiresAt() != null
                && notice.getExpiresAt()
                    .isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                "Cannot publish an expired notice");
        }

        notice.setStatus(
                NoticeStatus.PUBLISHED);

        notice.setPublishedAt(
                LocalDateTime.now());

        return mapToResponse(
                noticeRepository.save(notice));
    }

    @Override
    public NoticeResponse cancelNotice(
            Long noticeId) {

        Notice notice = findNotice(noticeId);

        if (notice.getStatus()
                == NoticeStatus.EXPIRED) {

            throw new IllegalArgumentException(
                "Expired notice cannot be cancelled");
        }

        if (notice.getStatus()
                == NoticeStatus.CANCELLED) {

            throw new IllegalArgumentException(
                "Notice is already cancelled");
        }

        notice.setStatus(
                NoticeStatus.CANCELLED);

        return mapToResponse(
                noticeRepository.save(notice));
    }

    @Override
    public int expireNotices() {

        List<Notice> notices =
                noticeRepository.findAll();

        int count = 0;

        for (Notice notice : notices) {

            if (notice.getStatus()
                    == NoticeStatus.PUBLISHED
                    && notice.getExpiresAt() != null
                    && notice.getExpiresAt()
                        .isBefore(LocalDateTime.now())) {

                notice.setStatus(
                        NoticeStatus.EXPIRED);

                noticeRepository.save(notice);

                count++;
            }
        }

        return count;
    }

    private Notice findNotice(Long id) {

        return noticeRepository
                .findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Notice not found with id: " + id));
    }

    private void validateCommunity(Long id) {

        if (!communityRepository.existsById(id)) {

            throw new ResourceNotFoundException(
                "Community not found with id: " + id);
        }
    }

    private List<NoticeResponse> mapList(
            List<Notice> notices) {

        return notices.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private NoticeResponse mapToResponse(
            Notice notice) {

        NoticeResponse response =
                new NoticeResponse();

        response.setNoticeId(
                notice.getNoticeId());

        response.setTitle(
                notice.getTitle());

        response.setMessage(
                notice.getMessage());

        response.setNoticeType(
                notice.getNoticeType());

        response.setPriority(
                notice.getPriority());

        response.setTargetType(
                notice.getTargetType());

        response.setStatus(
                notice.getStatus());

        response.setCommunityId(
                notice.getCommunity()
                    .getCommunityId());

        response.setCommunityName(
                notice.getCommunity()
                    .getName());

        if (notice.getBlock() != null) {

            response.setBlockId(
                    notice.getBlock()
                        .getBlockId());

            response.setBlockName(
                    notice.getBlock()
                        .getBlockName());
        }

        response.setPublishedAt(
                notice.getPublishedAt());

        response.setExpiresAt(
                notice.getExpiresAt());

        response.setCreatedAt(
                notice.getCreatedAt());

        return response;
    }
}