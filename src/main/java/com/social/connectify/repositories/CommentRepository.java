package com.social.connectify.repositories;

import com.social.connectify.models.Comment;
import com.social.connectify.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<List<Comment>> findAllByPost(Post post);
}
