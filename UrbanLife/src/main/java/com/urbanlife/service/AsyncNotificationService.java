package com.urbanlife.service;

import com.urbanlife.enums.NotificationPriority;
import com.urbanlife.enums.NotificationType;

public interface AsyncNotificationService {

    void sendNotificationAsync(
            Long userId,
            Long communityId,
            String title,
            String message,
            NotificationType type,
            NotificationPriority priority,
            String referenceType,
            Long referenceId);

    void sendCommunityNotificationAsync(
            Long communityId,
            String title,
            String message,
            NotificationType type,
            NotificationPriority priority,
            String referenceType,
            Long referenceId);
}