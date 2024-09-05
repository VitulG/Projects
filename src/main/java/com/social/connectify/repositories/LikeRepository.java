package com.social.connectify.repositories;

import com.social.connectify.models.Like;
import com.social.connectify.models.Post;
import com.social.connectify.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByPostAndUser(Post post, User user);
    Optional<List<Like>> findAllByPost(Post post);
}
