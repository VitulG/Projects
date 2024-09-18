package com.social.connectify.repositories;

import com.social.connectify.models.Notification;
import com.social.connectify.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page findByUser(User user, Pageable pageable);
}
