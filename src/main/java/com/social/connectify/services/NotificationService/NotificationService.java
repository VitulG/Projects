package com.social.connectify.services.NotificationService;

import com.social.connectify.dto.NotificationDto;
import com.social.connectify.exceptions.InvalidTokenException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    Page<NotificationDto> getAllNotifications(String token, Pageable pageable) throws InvalidTokenException;
}
