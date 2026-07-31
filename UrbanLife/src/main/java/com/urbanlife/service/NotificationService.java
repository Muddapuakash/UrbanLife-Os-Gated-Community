package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.NotificationRequest;
import com.urbanlife.dto.NotificationResponse;
import com.urbanlife.enums.NotificationType;

public interface NotificationService {

    NotificationResponse createNotification(
            NotificationRequest request);

    NotificationResponse getNotification(
            Long notificationId);

    List<NotificationResponse>
        getUserNotifications(
            Long userId);

    List<NotificationResponse>
        getUnreadNotifications(
            Long userId);

    List<NotificationResponse>
        getNotificationsByType(
            Long userId,
            NotificationType type);

    long getUnreadCount(
            Long userId);

    NotificationResponse markAsRead(
            Long notificationId,
            Long userId);

    int markAllAsRead(
            Long userId);

    void deleteNotification(
            Long notificationId,
            Long userId);
}