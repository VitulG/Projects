package com.social.connectify.services.NotificationService;

import com.social.connectify.dto.NotificationDto;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.models.Notification;
import com.social.connectify.models.Token;
import com.social.connectify.models.User;
import com.social.connectify.repositories.NotificationRepository;
import com.social.connectify.repositories.TokenRepository;
import com.social.connectify.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(TokenRepository tokenRepository, UserRepository userRepository,
                                   NotificationRepository notificationRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Page<NotificationDto> getAllNotifications(String token, Pageable pageable) throws InvalidTokenException {
        User user = validateUser(token);

        Page notifications = notificationRepository.findByUser(user, pageable);
        List<NotificationDto> notificationDtos = new ArrayList<NotificationDto>();

        for(Object notificationsPage : notifications) {
            Notification notification = (Notification) notificationsPage;
            notificationDtos.add(notification.convertToDto());
        }
        return new PageImpl<>(notificationDtos, pageable, notifications.getTotalElements());
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
