package com.social.connectify.repositories;

import com.social.connectify.models.FriendRequestStatus;
import com.social.connectify.models.Friendship;
import com.social.connectify.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Optional<Friendship> findByUserAndFriend(User user, User friend);
    List<Friendship> findAllFriendshipByStatus(FriendRequestStatus friendRequestStatus);
}
