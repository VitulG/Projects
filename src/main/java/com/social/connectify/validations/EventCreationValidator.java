package com.social.connectify.validations;

import com.social.connectify.dto.CreateEventDto;
import com.social.connectify.enums.EventVisibility;
import com.social.connectify.exceptions.EventCreationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EventCreationValidator {

    public void validateEventCreationDetails(CreateEventDto createEventDto) throws EventCreationException {
         isTitleValid(createEventDto.getTitle());
         isLocationValid(createEventDto.getLocation());
         isEventTimeValid(createEventDto.getStartTime(), createEventDto.getEndTime());
         isPrivacyValid(createEventDto.getEventVisibility());
    }

    private void isTitleValid(String title) throws EventCreationException {
        if(title == null || title.isEmpty()) {
            return;
        }

        if(title.length() < 5 || title.length() > 50) {
            throw new EventCreationException("title must be at least 5 and at most 50 characters long");
        }
    }

    private void isLocationValid(String location) throws EventCreationException {
        if(location == null || location.isEmpty()) {
            return;
        }

        if(location.length() < 5 || location.length() > 100) {
            throw new EventCreationException("location must be at least 5 and at most 100 characters long");
        }

        if (!location.matches("^[a-zA-Z0-9\\s,.-]+$")) {
            throw new EventCreationException("Location contains invalid characters.");
        }
    }

    private void isEventTimeValid(LocalDateTime startTime, LocalDateTime endTime) throws EventCreationException {
         if(startTime.isBefore(LocalDateTime.now())) {
             throw new EventCreationException("event date must be before future date");
         }

         if(endTime.isBefore(startTime)) {
             throw new EventCreationException("end time must be after start time");
         }
    }

    private void isPrivacyValid(String eventVisibility) throws EventCreationException {
        boolean isValid = eventVisibility.equalsIgnoreCase(EventVisibility.GROUP.toString()) || eventVisibility.equalsIgnoreCase(EventVisibility.PRIVATE.toString())
                || eventVisibility.equalsIgnoreCase(EventVisibility.PUBLIC.toString()) || eventVisibility.equalsIgnoreCase(EventVisibility.FRIENDS_ONLY.toString());

        if(!isValid) {
            throw new EventCreationException("Invalid event visibility");
        }
    }
}
