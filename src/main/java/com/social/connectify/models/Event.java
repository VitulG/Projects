package com.social.connectify.models;

import com.social.connectify.enums.EventUserRole;
import com.social.connectify.enums.EventVisibility;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Event extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    private String eventTitle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private EventVisibility visibility;
    private String eventDescription;
    private String eventLocation;

    @OneToOne(cascade = CascadeType.ALL)
    private Image eventCoverPhoto;

    @ManyToMany
    @JoinTable(
            name = "event_users",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> attendees;

    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE)
    private Set<EventAttendee> eventAttendees;

    @ManyToMany(mappedBy = "groupEvents")
    private Set<Group> groups;

    @ManyToOne(cascade = CascadeType.REMOVE)
    private User host;

}
