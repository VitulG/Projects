package com.social.connectify.repositories;

import com.social.connectify.models.EventAttendee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventAttendeeRepository extends JpaRepository<EventAttendee, Long> {
}
