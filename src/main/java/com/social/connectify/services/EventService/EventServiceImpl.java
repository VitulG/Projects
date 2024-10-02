package com.social.connectify.services.EventService;

import com.social.connectify.dto.CreateEventDto;
import com.social.connectify.dto.SendInvitationGroupRequestDto;
import com.social.connectify.dto.UserEventsDto;
import com.social.connectify.enums.EventUserRole;
import com.social.connectify.enums.EventVisibility;
import com.social.connectify.enums.GroupUserRole;
import com.social.connectify.enums.NotificationStatus;
import com.social.connectify.exceptions.*;
import com.social.connectify.models.*;
import com.social.connectify.repositories.*;
import com.social.connectify.validations.EventCreationValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EventServiceImpl implements EventService {
    private final TokenRepository tokenRepository;
    private final EventCreationValidator eventCreationValidator;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventAttendeeRepository eventAttendeeRepository;
    private final NotificationRepository notificationRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public EventServiceImpl(TokenRepository tokenRepository, EventCreationValidator eventCreation, ImageRepository imageRepository,
                            UserRepository userRepository, EventRepository eventRepository, EventAttendeeRepository eventAttendeeRepository,
                            NotificationRepository notificationRepository, GroupRepository groupRepository) {
        this.tokenRepository = tokenRepository;
        this.eventCreationValidator = eventCreation;
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.eventAttendeeRepository = eventAttendeeRepository;
        this.notificationRepository = notificationRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    @Transactional
    public String createEvent(String token, CreateEventDto createEventDto) throws InvalidTokenException, EventCreationException, GroupNotFoundException, UserNotFoundException {
        User user = validAndGetUser(token);
        eventCreationValidator.validateEventCreationDetails(createEventDto);

        Event event = new Event();
        event.setEventTitle(createEventDto.getTitle());
        event.setEventDescription(createEventDto.getDescription());
        event.setEventLocation(createEventDto.getLocation());
        event.setStartTime(createEventDto.getStartTime());
        event.setVisibility(getVisibility(createEventDto.getEventVisibility()));
        event.setEndTime(createEventDto.getEndTime());
        event.setCreatedAt(LocalDateTime.now());
        event.setHost(user);

        if(user.getUserEvents() == null) {
            user.setUserEvents(new HashSet<>());
        }
        user.getUserEvents().add(event);

        if(user.getEventsToHost() == null) {
            user.setEventsToHost(new ArrayList<>());
        }
        user.getEventsToHost().add(event);

        if(event.getAttendees() == null) {
            event.setAttendees(new HashSet<>());
        }
        event.getAttendees().add(user);

        Image eventCoverImage = createEventCoverImage(event, createEventDto.getImage());
        if(eventCoverImage != null) {
            event.setEventCoverPhoto(eventCoverImage);
        }

        EventAttendee eventAttendee = new EventAttendee();
        eventAttendee.setRsvp(LocalDateTime.now());
        eventAttendee.setUserRole(EventUserRole.HOST);
        eventAttendee.setEvent(event);
        eventAttendee.setCreatedAt(LocalDateTime.now());
        eventAttendeeRepository.save(eventAttendee);

        if(event.getEventAttendees() == null) {
            event.setEventAttendees(new HashSet<>());
        }
        event.getEventAttendees().add(eventAttendee);

        eventRepository.save(event);
        // set attendees and co hosts
        assignCoHostToEvent(event, createEventDto.getCo_hosts(), user);

        return "Event "+event.getEventTitle()+" created";
    }

    @Override
    public String sendInvitationToGroups(String token, Long eventId, SendInvitationGroupRequestDto groups) throws InvalidTokenException, EventNotFoundException, UnauthorizedUserException, IllegalGroupListException, GroupNotFoundException {
        User user = validAndGetUser(token);
        Event event = eventRepository.findEventByEventId(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event does not exist"));

        boolean isAttendee = event.getAttendees()
                .stream().findAny()
                .isEmpty();

        if(!isAttendee) {
            throw new UnauthorizedUserException("event can be shared only by host or by attendees only");
        }

        if(groups.getGroupIds() == null || groups.getGroupIds().isEmpty()) {
            throw new IllegalGroupListException("at least one group is required");
        }

        sendGroupInvitation(groups.getGroupIds(), user, event);

        return "invitation sent";

    }

    @Override
    public List<UserEventsDto> getUserEvents(String token) throws InvalidTokenException {
        User user = validAndGetUser(token);

        List<UserEventsDto> userEventsDtos = new ArrayList<>();

        for(Event event : user.getUserEvents()) {
            if(!event.getEndTime().isBefore(LocalDateTime.now())) {
                UserEventsDto eventDto = getEventDto(event);
                userEventsDtos.add(eventDto);
            }
        }
        return userEventsDtos;

    }

    @Override
    public List<UserEventsDto> getUserHostEvents(String token) throws InvalidTokenException {
        User user = validAndGetUser(token);

        List<UserEventsDto> userEventsHostDtos = new ArrayList<>();

        for(Event event : user.getEventsToHost()) {
            if(!event.getEndTime().isBefore(LocalDateTime.now())) {
                UserEventsDto userEvents = getEventDto(event);
                userEventsHostDtos.add(userEvents);
            }
        }
        return userEventsHostDtos;
    }

    private UserEventsDto getEventDto(Event event) {
        UserEventsDto eventDto = new UserEventsDto();
        eventDto.setEventName(event.getEventTitle());
        eventDto.setEventDescription(event.getEventDescription());
        eventDto.setStartTime(event.getStartTime());
        eventDto.setEndTime(event.getEndTime());
        eventDto.setLocation(event.getEventLocation());
        eventDto.setImageUrl(event.getEventCoverPhoto().getImageUrl());
        return eventDto;
    }

    private void sendGroupInvitation(List<Long> groupIds, User sender, Event event) throws GroupNotFoundException {
        for(Long groupId : groupIds) {
            Group group = groupRepository.findByGroupId(groupId)
                    .orElseThrow(() -> new GroupNotFoundException("Group does not exist"));

            if(group.getGroupEvents() == null) {
                group.setGroupEvents(new HashSet<>());
            }
            group.getGroupEvents().add(event);

            List<GroupMembership> admins = group.getGroupMemberships()
                    .stream()
                    .filter(members -> members.getRole() == GroupUserRole.ADMIN)
                    .toList();

            if(!admins.isEmpty()) {
                for(GroupMembership admin : admins) {
                    createNotificationForCoHost(event, admin.getUser(), sender);
                }
            }
        }
    }

    private void createNotificationForGroupAdmins(Event event, User groupAdmin, User sender) {
        Notification notification = new Notification();
        notification.setCreatedAt(LocalDateTime.now());
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setUser(groupAdmin);
        notification.setMessage(sender.getFirstName()+" "+sender.getLastName()+" has sent the invitation of the event "
            + event.getEventTitle()+" to your group.");
        notificationRepository.save(notification);
    }

    private Image createEventCoverImage(Event event, String imageUrl) {
        if(imageUrl != null && !imageUrl.isEmpty()) {
            Image image = new Image();
            image.setImageUrl(imageUrl);
            image.setEvent(event);
            image.setCreatedAt(LocalDateTime.now());
            event.setEventCoverPhoto(image);
            return imageRepository.save(image);
        }
        return null;
    }

    private void assignCoHostToEvent(Event event, List<String> coHostEmails, User admin) throws UserNotFoundException {
        if(coHostEmails != null && !coHostEmails.isEmpty()) {
            for(String userEmail : coHostEmails) {
                User user = userRepository.findUserByEmail(userEmail)
                        .orElseThrow(() -> new UserNotFoundException("user does not exist"));
                EventAttendee eventAttendee = new EventAttendee();
                eventAttendee.setCreatedAt(LocalDateTime.now());
                eventAttendee.setRsvp(LocalDateTime.now());
                eventAttendee.setUserRole(EventUserRole.CO_HOST);
                eventAttendee.setEvent(event);
                eventAttendeeRepository.save(eventAttendee);

                event.getEventAttendees().add(eventAttendee);
                event.getAttendees().add(user);

                if(user.getUserEvents() == null) {
                    user.setUserEvents(new HashSet<>());
                }
                user.getUserEvents().add(event);
                createNotificationForCoHost(event, admin, user);
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
