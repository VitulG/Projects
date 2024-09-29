package com.social.connectify.repositories;

import com.social.connectify.models.Group;
import com.social.connectify.models.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m JOIN m.groups g WHERE g = :group")
    Page<Message> findByGroup(@Param("group") Group group, Pageable pageable);

}
