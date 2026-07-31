package com.urbanlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.Notification;
import com.urbanlife.enums.NotificationType;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification>
        findByUserUserIdOrderByCreatedAtDesc(
            Long userId);

    List<Notification>
        findByUserUserIdAndIsReadFalseOrderByCreatedAtDesc(
            Long userId);

    List<Notification>
        findByUserUserIdAndTypeOrderByCreatedAtDesc(
            Long userId,
            NotificationType type);

    long countByUserUserIdAndIsReadFalse(
            Long userId);
}