package com.fundbridge.notification.service.impl;

import com.fundbridge.common.exception.ResourceNotFoundException;
import com.fundbridge.notification.dto.NotificationRequest;
import com.fundbridge.notification.entity.Notification;
import com.fundbridge.notification.repository.NotificationRepository;
import com.fundbridge.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public Notification sendNotification(NotificationRequest request) {
        log.info("Sending notification to {}: {}", request.getUserEmail(), request.getTitle());

        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .userEmail(request.getUserEmail())
                .title(request.getTitle())
                .message(request.getMessage())
                .notificationType(request.getNotificationType())
                .isRead(false)
                .build();

        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getMyNotifications(String email) {
        return notificationRepository.findByUserEmailOrderByCreatedAtDesc(email);
    }

    @Override
    public List<Notification> getUnreadNotifications(String email) {
        return notificationRepository.findByUserEmailAndIsReadFalse(email);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
