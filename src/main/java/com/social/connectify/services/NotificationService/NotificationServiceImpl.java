package com.social.connectify.services.NotificationService;

import com.social.connectify.dto.NotificationDto;
import com.social.connectify.enums.NotificationStatus;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.NotificationAlreadyMarkedException;
import com.social.connectify.exceptions.NotificationNotFoundException;
import com.social.connectify.exceptions.UnauthorizedUserException;
import com.social.connectify.models.*;
import com.social.connectify.repositories.NotificationRepository;
import com.social.connectify.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final TokenRepository tokenRepository;
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(TokenRepository tokenRepository,
                                   NotificationRepository notificationRepository) {
        this.tokenRepository = tokenRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Page<NotificationDto> getAllNotifications(String token, Pageable pageable) throws InvalidTokenException {
        User user = validateUser(token);

        Page<Notification> notifications = notificationRepository.findByUser(user, pageable);
        List<NotificationDto> notificationDtos = notifications.stream()
                .filter(notification -> !notification.isDeleted())
                .map(Notification::convertToDto)
               .collect(Collectors.toList());

        return new PageImpl<>(notificationDtos, pageable, notifications.getTotalElements());
    }

    @Override
    public Page<NotificationDto> getAllUnreadNotifications(String token, Pageable pageable) throws InvalidTokenException {
        User user = validateUser(token);
        Page<Notification> unreadNotifications = notificationRepository.findByUserAndStatus(user, NotificationStatus.UNREAD, pageable);

        List<NotificationDto> unreadNotificationsList = unreadNotifications.stream()
                .filter(notification -> !notification.isDeleted())
                .map(Notification::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(unreadNotificationsList, pageable, unreadNotifications.getTotalElements());
    }

    @Override
    public void markAsRead(String token, Long notificationId) throws InvalidTokenException, NotificationNotFoundException, UnauthorizedUserException, NotificationAlreadyMarkedException {
        User user = validateUser(token);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("notification not found"));

        if(!notification.getUser().equals(user)) {
            throw new UnauthorizedUserException("user not authorized to view or modify notification");
        }

        if(notification.getStatus() != NotificationStatus.READ && !notification.isDeleted()) {
            notification.setStatus(NotificationStatus.READ);
            notificationRepository.save(notification);
        }else {
            throw new NotificationAlreadyMarkedException("this notification is already marked");
        }
    }

    @Override
    public String deleteNotification(String token, Long notificationId) throws InvalidTokenException, NotificationNotFoundException, UnauthorizedUserException {
        User user = validateUser(token);

        // Try to find the notification by ID
        Notification notification = notificationRepository.findById(notificationId)
                .orElse(null);

        // If the notification is not found or already deleted
        if (notification == null || notification.isDeleted() || notification.getStatus() == NotificationStatus.DELETED) {
            throw new NotificationNotFoundException("Notification either deleted or not found");
        }

        // Check if the notification belongs to the authenticated user
        if (!notification.getUser().equals(user)) {
            throw new UnauthorizedUserException("User not authorized to view or modify this notification");
        }

        // Mark the notification as deleted
        notification.setDeleted(true);
        notification.setStatus(NotificationStatus.DELETED);
        notificationRepository.save(notification);

        return "Notification deleted successfully";
    }

    private User validateUser(String token) throws InvalidTokenException {
        Optional<Token> optionalToken = tokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            throw new InvalidTokenException("token not found");
        }

        Token existingToken = optionalToken.get();

        if (!existingToken.isActive() || existingToken.getExpireAt().isBefore(LocalDateTime.now()) ||
                existingToken.isDeleted()) {
            throw new InvalidTokenException("either the token is not active or expired");
        }
        return existingToken.getUser();
    }
}
