package com.social.connectify.services.EventService;

import com.social.connectify.dto.CreateEventDto;
import com.social.connectify.enums.EventUserRole;
import com.social.connectify.enums.EventVisibility;
import com.social.connectify.enums.NotificationStatus;
import com.social.connectify.exceptions.EventCreationException;
import com.social.connectify.exceptions.GroupNotFoundException;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.UserNotFoundException;
import com.social.connectify.models.*;
import com.social.connectify.repositories.*;
import com.social.connectify.validations.EventCreationValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {
    private final TokenRepository tokenRepository;
    private final EventCreationValidator eventCreationValidator;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventAttendeeRepository eventAttendeeRepository;
    private final NotificationRepository notificationRepository;

    @Autowired
    public EventServiceImpl(TokenRepository tokenRepository, EventCreationValidator eventCreation, ImageRepository imageRepository,
                            UserRepository userRepository, EventRepository eventRepository, EventAttendeeRepository eventAttendeeRepository,
                            NotificationRepository notificationRepository) {
        this.tokenRepository = tokenRepository;
        this.eventCreationValidator = eventCreation;
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.eventAttendeeRepository = eventAttendeeRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public String createEvent(String token, CreateEventDto createEventDto) throws InvalidTokenException, EventCreationException, GroupNotFoundException, UserNotFoundException {
        User user = validAndGetUser(token);
        eventCreationValidator.validateEventCreationDetails(createEventDto);

        Event event = new Event();
        event.setEventDescription(createEventDto.getDescription());
        event.setEventLocation(createEventDto.getLocation());
        event.setEventTitle(createEventDto.getTitle());
        EventVisibility visibility = getVisibility(createEventDto.getEventVisibility());
        event.setVisibility(visibility);

        EventAttendee eventAttendee = new EventAttendee();
        eventAttendeeRepository.save(eventAttendee);
        eventAttendee.setEvent(event);
        eventAttendee.setRsvp(LocalDateTime.now());
        eventAttendee.setUser(user);
        eventAttendee.setUserRole(EventUserRole.HOST);

        if(user.getEvents() == null) {
            user.setEvents(new ArrayList<>());
        }
        user.getEvents().add(eventAttendee);
        userRepository.save(user);

        if(event.getEventAttendees() == null) {
            event.setEventAttendees(new ArrayList<>());
        }
        event.getEventAttendees().add(eventAttendee);

        event.setStartTime(createEventDto.getStartTime());
        event.setEndTime(createEventDto.getEndTime());
        createEventCoverImage(event, createEventDto.getImage());
        eventRepository.save(event);
        assignCoHostToEvent(event, createEventDto.getCo_hosts(), user);

        return "event "+event.getEventTitle()+" created";
    }

    private void createEventCoverImage(Event event, String imageUrl) {
        if(imageUrl != null && !imageUrl.isEmpty()) {
            Image image = new Image();
            image.setImageUrl(imageUrl);
            image.setEvent(event);
            image.setCreatedAt(LocalDateTime.now());
            event.setEventCoverPhoto(image);
            imageRepository.save(image);
        }
    }

    private void assignCoHostToEvent(Event event, List<String> coHostEmails, User admin) throws UserNotFoundException {
        if(coHostEmails != null && !coHostEmails.isEmpty()) {
            for(String email : coHostEmails) {
                User user = userRepository.findUserByEmail(email)
                        .orElseThrow(() -> new UserNotFoundException("user not found"));

                if(user.getEvents() == null) {
                    user.setEvents(new ArrayList<>());
                }
                EventAttendee eventAttendee = new EventAttendee();
                eventAttendee.setUser(user);
                eventAttendee.setRsvp(LocalDateTime.now());
                eventAttendee.setUserRole(EventUserRole.CO_HOST);
                eventAttendee.setEvent(event);
                eventAttendeeRepository.save(eventAttendee);

                createNotificationForCoHost(event, admin, user);

                user.getEvents().add(eventAttendee);
                userRepository.save(user);
            }
        }
    }

    private void createNotificationForCoHost(Event event, User admin, User coHost) {
        Notification notification = new Notification();
        notification.setCreatedAt(LocalDateTime.now());
        notification.setMessage(admin.getFirstName()+" "+admin.getLastName()+" added you in the event "
        + event.getEventTitle()+" as a co-host");
        notification.setUser(coHost);
        notification.setStatus(NotificationStatus.UNREAD);
        notificationRepository.save(notification);
    }

    private EventVisibility getVisibility(String visibility) {
        // convert visibility string to EventVisibility enum
        return switch (visibility.toLowerCase()) {
            case "public" -> EventVisibility.PUBLIC;
            case "private" -> EventVisibility.PRIVATE;
            case "group" -> EventVisibility.GROUP;
            case "friends_only" -> EventVisibility.FRIENDS_ONLY;
            default -> throw new IllegalArgumentException("Invalid visibility value");
        };
    }


    private User validAndGetUser(String token) throws InvalidTokenException {
        // validate token and get user from database
        Optional<Token> optionalToken = tokenRepository.findByToken(token);

        if(optionalToken.isEmpty()) {
            throw new InvalidTokenException("token not found");
        }

        Token existingToken = optionalToken.get();

        if(!existingToken.isActive() || existingToken.getExpireAt().isBefore(java.time.LocalDateTime.now()) ||
                existingToken.isDeleted()) {
            throw new InvalidTokenException("either the token is not active or expired");
        }
        return existingToken.getUser();
    }
}
