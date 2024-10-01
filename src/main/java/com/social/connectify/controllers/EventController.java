package com.social.connectify.controllers;

import com.social.connectify.dto.CreateEventDto;
import com.social.connectify.exceptions.EventCreationException;
import com.social.connectify.exceptions.GroupNotFoundException;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.UserNotFoundException;
import com.social.connectify.services.EventService.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private ResponseEntity<String> createEvent(@RequestHeader("Authorization") String token,
                                               @RequestBody CreateEventDto createEventDto) {
        try {
            String response = eventService.createEvent(token, createEventDto);
            return ResponseEntity.ok(response);
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

}
