package com.social.connectify.repositories;

import com.social.connectify.models.GroupMembership;
import com.social.connectify.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long> {
    Optional<GroupMembership> findGroupMembershipByUser(User user);
}
