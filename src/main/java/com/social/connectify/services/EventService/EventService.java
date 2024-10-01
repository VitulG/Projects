package com.social.connectify.services.EventService;

import com.social.connectify.dto.CreateEventDto;
import com.social.connectify.exceptions.EventCreationException;
import com.social.connectify.exceptions.GroupNotFoundException;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.UserNotFoundException;

public interface EventService {
    String createEvent(String token, CreateEventDto createEventDto) throws InvalidTokenException, EventCreationException, GroupNotFoundException, UserNotFoundException;
}
