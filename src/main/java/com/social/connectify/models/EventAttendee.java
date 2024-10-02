package com.social.connectify.models;

import com.social.connectify.enums.EventUserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class EventAttendee extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventAttendeeId;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Event event;

    @Enumerated(EnumType.STRING)
    private EventUserRole userRole;

    private LocalDateTime rsvp;
}
