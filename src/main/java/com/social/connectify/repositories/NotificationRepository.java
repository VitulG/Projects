package com.social.connectify.repositories;

import com.social.connectify.models.MessageStatus;
import com.social.connectify.models.Notification;
import com.social.connectify.models.NotificationStatus;
import com.social.connectify.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUser(User user, Pageable pageable);
    Page<Notification> findByUserAndStatus(User user, NotificationStatus status, Pageable pageable);
}
