package com.social.connectify.controllers;

import com.social.connectify.dto.NotificationDto;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.NotificationAlreadyMarkedException;
import com.social.connectify.exceptions.NotificationNotFoundException;
import com.social.connectify.exceptions.UnauthorizedUserException;
import com.social.connectify.services.NotificationService.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/getAllNotifications")
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

    @GetMapping("/getUnreadNotifications")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(@RequestHeader("Authorization") String token,
                                                                        Pageable pageable) {
        try {
            Page<NotificationDto> unreadNotifications = notificationService.getAllUnreadNotifications(token, pageable);
            List<NotificationDto> response = unreadNotifications.getContent();
            return ResponseEntity.ok(response);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{notificationId}/markAsRead")
    public ResponseEntity<Void> markNotificationAsRead(@RequestHeader("Authorization") String token,
                                                             @PathVariable("notificationId") Long notificationId) {
        try {
            notificationService.markAsRead(token, notificationId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InvalidTokenException | UnauthorizedUserException exception) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (NotificationNotFoundException notFoundException) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotificationAlreadyMarkedException alreadyMarkedException) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{notificationId}/deleteNotification")
    public ResponseEntity<String> deleteNotification(@RequestHeader("Authorization") String token,
                                                      @PathVariable("notificationId") Long notificationId) {
        try {
            String response = notificationService.deleteNotification(token, notificationId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (InvalidTokenException | UnauthorizedUserException unauthorizedException) {
            return new ResponseEntity<>(unauthorizedException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (NotificationNotFoundException notfoundException) {
            return new ResponseEntity<>(notfoundException.getMessage(), HttpStatus.NOT_FOUND);
        } catch(Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
