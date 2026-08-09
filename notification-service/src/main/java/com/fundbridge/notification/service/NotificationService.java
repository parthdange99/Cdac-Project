package com.fundbridge.notification.service;

import com.fundbridge.notification.dto.NotificationRequest;
import com.fundbridge.notification.entity.Notification;

import java.util.List;

public interface NotificationService {
    Notification sendNotification(NotificationRequest request);
    List<Notification> getMyNotifications(String email);
    List<Notification> getUnreadNotifications(String email);
    void markAsRead(Long notificationId);
}
