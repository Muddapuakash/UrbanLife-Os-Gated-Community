package com.urbanlife.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urbanlife.dto.NotificationRequest;
import com.urbanlife.dto.NotificationResponse;
import com.urbanlife.entity.Community;
import com.urbanlife.entity.Notification;
import com.urbanlife.entity.User;
import com.urbanlife.enums.NotificationPriority;
import com.urbanlife.enums.NotificationType;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.repository.NotificationRepository;
import com.urbanlife.repository.UserRepository;
import com.urbanlife.service.NotificationService;

@Service
@Transactional
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository
            notificationRepository;

    private final UserRepository userRepository;

    private final CommunityRepository
            communityRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            CommunityRepository communityRepository) {

        this.notificationRepository =
                notificationRepository;

        this.userRepository =
                userRepository;

        this.communityRepository =
                communityRepository;
    }

    @Override
    public NotificationResponse createNotification(
            NotificationRequest request) {

        User user =
                findUser(request.getUserId());

        Community community = null;

        if (request.getCommunityId() != null) {

            community =
                    findCommunity(
                        request.getCommunityId());
        }

        Notification notification =
                new Notification();

        notification.setTitle(
                request.getTitle());

        notification.setMessage(
                request.getMessage());

        notification.setType(
                request.getType());

        notification.setPriority(
                request.getPriority() == null
                    ? NotificationPriority.NORMAL
                    : request.getPriority());

        notification.setUser(user);

        notification.setCommunity(
                community);

        notification.setReferenceType(
                request.getReferenceType());

        notification.setReferenceId(
                request.getReferenceId());

        return mapToResponse(
                notificationRepository
                    .save(notification));
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotification(
            Long notificationId) {

        return mapToResponse(
                findNotification(
                    notificationId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse>
            getUserNotifications(
                Long userId) {

        findUser(userId);

        return notificationRepository
                .findByUserUserIdOrderByCreatedAtDesc(
                    userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse>
            getUnreadNotifications(
                Long userId) {

        findUser(userId);

        return notificationRepository
                .findByUserUserIdAndIsReadFalseOrderByCreatedAtDesc(
                    userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse>
            getNotificationsByType(
                Long userId,
                NotificationType type) {

        findUser(userId);

        return notificationRepository
                .findByUserUserIdAndTypeOrderByCreatedAtDesc(
                    userId,
                    type)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(
            Long userId) {

        findUser(userId);

        return notificationRepository
                .countByUserUserIdAndIsReadFalse(
                    userId);
    }

    @Override
    public NotificationResponse markAsRead(
            Long notificationId,
            Long userId) {

        Notification notification =
                findNotification(
                    notificationId);

        validateOwner(
                notification,
                userId);

        if (!notification.isRead()) {

            notification.setRead(true);

            notification.setReadAt(
                    LocalDateTime.now());
        }

        return mapToResponse(
                notificationRepository
                    .save(notification));
    }

    @Override
    public int markAllAsRead(
            Long userId) {

        findUser(userId);

        List<Notification> notifications =
                notificationRepository
                    .findByUserUserIdAndIsReadFalseOrderByCreatedAtDesc(
                        userId);

        LocalDateTime now =
                LocalDateTime.now();

        for (Notification notification :
                notifications) {

            notification.setRead(true);
            notification.setReadAt(now);
        }

        notificationRepository
                .saveAll(notifications);

        return notifications.size();
    }

    @Override
    public void deleteNotification(
            Long notificationId,
            Long userId) {

        Notification notification =
                findNotification(
                    notificationId);

        validateOwner(
                notification,
                userId);

        notificationRepository
                .delete(notification);
    }

    private Notification findNotification(
            Long notificationId) {

        return notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Notification not found with id: "
                        + notificationId));
    }

    private User findUser(
            Long userId) {

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "User not found with id: "
                        + userId));
    }

    private Community findCommunity(
            Long communityId) {

        return communityRepository
                .findById(communityId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Community not found with id: "
                        + communityId));
    }

    private void validateOwner(
            Notification notification,
            Long userId) {

        if (!notification
                .getUser()
                .getUserId()
                .equals(userId)) {

            throw new IllegalArgumentException(
                "User does not own this notification");
        }
    }

    private NotificationResponse mapToResponse(
            Notification notification) {

        NotificationResponse response =
                new NotificationResponse();

        response.setNotificationId(
                notification
                    .getNotificationId());

        response.setTitle(
                notification.getTitle());

        response.setMessage(
                notification.getMessage());

        response.setType(
                notification.getType());

        response.setPriority(
                notification.getPriority());

        response.setUserId(
                notification
                    .getUser()
                    .getUserId());

        if (notification.getCommunity()
                != null) {

            response.setCommunityId(
                    notification
                        .getCommunity()
                        .getCommunityId());
        }

        response.setReferenceType(
                notification
                    .getReferenceType());

        response.setReferenceId(
                notification
                    .getReferenceId());

        response.setRead(
                notification.isRead());

        response.setReadAt(
                notification.getReadAt());

        response.setCreatedAt(
                notification.getCreatedAt());

        return response;
    }
}