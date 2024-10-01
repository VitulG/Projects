package com.social.connectify.models;

import com.social.connectify.enums.EventUserRole;
import com.social.connectify.enums.EventVisibility;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    private String eventTitle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private EventVisibility visibility;
    private String eventDescription;
    private String eventLocation;

    @OneToOne
    private Image eventCoverPhoto;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventAttendee> eventAttendees;

}
