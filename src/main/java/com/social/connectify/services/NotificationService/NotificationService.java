package com.social.connectify.services.NotificationService;

import com.social.connectify.dto.NotificationDto;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.NotificationAlreadyMarkedException;
import com.social.connectify.exceptions.NotificationNotFoundException;
import com.social.connectify.exceptions.UnauthorizedUserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    Page<NotificationDto> getAllNotifications(String token, Pageable pageable) throws InvalidTokenException;
    Page<NotificationDto> getAllUnreadNotifications(String token, Pageable pageable) throws InvalidTokenException;
    void markAsRead(String token, Long notificationId) throws InvalidTokenException, NotificationNotFoundException, UnauthorizedUserException, NotificationAlreadyMarkedException;
    String deleteNotification(String token, Long notificationId) throws InvalidTokenException, NotificationNotFoundException, UnauthorizedUserException;
}
