package com.social.connectify.controllers;

import com.social.connectify.dto.CreateEventDto;
import com.social.connectify.dto.SendInvitationGroupRequestDto;
import com.social.connectify.dto.UserEventsDto;
import com.social.connectify.exceptions.*;
import com.social.connectify.services.EventService.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    // Controller for event management
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/create-event")
    public ResponseEntity<String> createEvent(@RequestHeader("Authorization") String token,
                                               @RequestBody CreateEventDto createEventDto) {
        try {
            String response = eventService.createEvent(token, createEventDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(invalidTokenException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (EventCreationException creationException) {
            return new ResponseEntity<>(creationException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch(GroupNotFoundException | UserNotFoundException notFoundException) {
            return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/event-invite-groups/{eventId}")
    public ResponseEntity<String> sendInvitationToGroup(@RequestHeader("Authorization") String token, @PathVariable("eventId") Long eventId,
                                                        @RequestBody SendInvitationGroupRequestDto groups) {
        try {
            String response = eventService.sendInvitationToGroups(token, eventId, groups);
            return ResponseEntity.ok(response);
        } catch (InvalidTokenException | UnauthorizedUserException securityException) {
            return new ResponseEntity<>(securityException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (EventNotFoundException | GroupNotFoundException notFoundException) {
            return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.NOT_FOUND);
        } catch(IllegalGroupListException illegalGroupListException) {
            return new ResponseEntity<>(illegalGroupListException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-user-events")
    public ResponseEntity<List<UserEventsDto>> getUserEvents(@RequestHeader("Authorization") String token) {
        try {
            List<UserEventsDto> userEventsDtos = eventService.getUserEvents(token);
            return ResponseEntity.ok(userEventsDtos);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-user-host-events")
    public ResponseEntity<List<UserEventsDto>> getUserHostEvents(@RequestHeader("Authorization") String token) {
        try {
            List<UserEventsDto> response = eventService.getUserHostEvents(token);
            return ResponseEntity.ok(response);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
