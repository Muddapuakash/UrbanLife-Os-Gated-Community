package com.urbanlife.serviceimpl;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.urbanlife.dto.NotificationRequest;
import com.urbanlife.entity.Resident;
import com.urbanlife.enums.NotificationPriority;
import com.urbanlife.enums.NotificationType;
import com.urbanlife.enums.ResidentStatus;
import com.urbanlife.repository.ResidentRepository;
import com.urbanlife.service.AsyncNotificationService;
import com.urbanlife.service.NotificationService;

@Service
public class AsyncNotificationServiceImpl
        implements AsyncNotificationService {

    private final NotificationService notificationService;

    private final ResidentRepository residentRepository;

    public AsyncNotificationServiceImpl(
            NotificationService notificationService,
            ResidentRepository residentRepository) {

        this.notificationService =
                notificationService;

        this.residentRepository =
                residentRepository;
    }

    // ==========================================
    // INDIVIDUAL ASYNC NOTIFICATION
    // ==========================================

    @Override
    @Async("notificationExecutor")
    public void sendNotificationAsync(
            Long userId,
            Long communityId,
            String title,
            String message,
            NotificationType type,
            NotificationPriority priority,
            String referenceType,
            Long referenceId) {

        System.out.println(
                "Individual notification started on thread: "
                + Thread.currentThread().getName());

        try {

            NotificationRequest request =
                    new NotificationRequest();

            request.setUserId(userId);

            request.setCommunityId(
                    communityId);

            request.setTitle(title);

            request.setMessage(message);

            request.setType(type);

            request.setPriority(priority);

            request.setReferenceType(
                    referenceType);

            request.setReferenceId(
                    referenceId);

            notificationService
                    .createNotification(request);

            System.out.println(
                    "Individual notification completed on thread: "
                    + Thread.currentThread().getName());

        } catch (Exception e) {

            System.err.println(
                    "Failed to create async notification for user "
                    + userId
                    + ": "
                    + e.getMessage());
        }
    }

    // ==========================================
    // BULK COMMUNITY ASYNC NOTIFICATION
    // ==========================================

    @Override
    @Async("notificationExecutor")
    public void sendCommunityNotificationAsync(
            Long communityId,
            String title,
            String message,
            NotificationType type,
            NotificationPriority priority,
            String referenceType,
            Long referenceId) {

        System.out.println(
                "Bulk notification started on thread: "
                + Thread.currentThread().getName());

        try {

            List<Resident> residents =
                    residentRepository
                        .findByFlatBlockCommunityCommunityIdAndStatus(
                            communityId,
                            ResidentStatus.ACTIVE);

            System.out.println(
                    "Active residents found in community "
                    + communityId
                    + ": "
                    + residents.size());

            int successCount = 0;
            int failureCount = 0;

            for (Resident resident : residents) {

                try {

                    NotificationRequest request =
                            new NotificationRequest();

                    request.setUserId(
                            resident
                                .getUser()
                                .getUserId());

                    request.setCommunityId(
                            communityId);

                    request.setTitle(title);

                    request.setMessage(message);

                    request.setType(type);

                    request.setPriority(priority);

                    request.setReferenceType(
                            referenceType);

                    request.setReferenceId(
                            referenceId);

                    notificationService
                            .createNotification(request);

                    successCount++;

                } catch (Exception e) {

                    failureCount++;

                    System.err.println(
                            "Failed notification for resident "
                            + resident.getResidentId()
                            + ": "
                            + e.getMessage());
                }
            }

            System.out.println(
                    "Bulk notification completed.");

            System.out.println(
                    "Thread: "
                    + Thread.currentThread().getName());

            System.out.println(
                    "Success: "
                    + successCount);

            System.out.println(
                    "Failed: "
                    + failureCount);

        } catch (Exception e) {

            System.err.println(
                    "Bulk notification failed for community "
                    + communityId
                    + ": "
                    + e.getMessage());
        }
    }
}