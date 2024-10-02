package com.social.connectify.services.EventService;

import com.social.connectify.dto.CreateEventDto;
import com.social.connectify.dto.SendInvitationGroupRequestDto;
import com.social.connectify.dto.UserEventsDto;
import com.social.connectify.exceptions.*;

import java.util.List;

public interface EventService {
    String createEvent(String token, CreateEventDto createEventDto) throws InvalidTokenException, EventCreationException, GroupNotFoundException, UserNotFoundException;
    String sendInvitationToGroups(String token, Long eventId, SendInvitationGroupRequestDto groups) throws InvalidTokenException, EventNotFoundException, UnauthorizedUserException, IllegalGroupListException, GroupNotFoundException;
    List<UserEventsDto> getUserEvents(String token) throws InvalidTokenException;
    List<UserEventsDto> getUserHostEvents(String token) throws InvalidTokenException;
}
