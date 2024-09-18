package com.social.connectify.controllers;

import com.social.connectify.dto.NotificationDto;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.services.NotificationService.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/getNotifications")
    public ResponseEntity<List<NotificationDto>> getNotifications(@RequestHeader("Authorization") String token,
                                                                  Pageable pageable) {
        try {
            List<NotificationDto> response = notificationService.getAllNotifications(token, pageable).getContent();
            return ResponseEntity.ok(response);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        }catch (Exception ex) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
