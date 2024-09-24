package com.social.connectify.repositories;

import com.social.connectify.models.GroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long> {
}
