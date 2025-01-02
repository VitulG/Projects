package com.govt.irctc.service.notificationservice;

import com.govt.irctc.model.User;

public interface NotificationService {
    public void createNotification(User user, String message, String title);
}
