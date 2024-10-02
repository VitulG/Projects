package com.social.connectify.repositories;

import com.social.connectify.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findEventByEventId(Long eventId);
}
