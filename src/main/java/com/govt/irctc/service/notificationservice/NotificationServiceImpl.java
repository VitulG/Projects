package com.govt.irctc.service.notificationservice;

import com.govt.irctc.model.Notification;
import com.govt.irctc.model.User;
import com.govt.irctc.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void createNotification(User user, String message, String title) {
        Notification notification = new Notification();

        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setUser(user);

        if(user.getUserNotifications() == null) {
            user.setUserNotifications(new ArrayList<>());
        }
        user.getUserNotifications().add(notification);

        notificationRepository.save(notification);
    }
}
