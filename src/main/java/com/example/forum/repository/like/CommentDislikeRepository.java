package com.example.forum.repository.like;

import com.example.forum.model.comment.Comment;
import com.example.forum.model.like.CommentDislike;
import com.example.forum.model.like.CommentLike;
import com.example.forum.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentDislikeRepository extends JpaRepository<CommentDislike, Long> {

    Optional<CommentDislike> findByCommentAndUser(Comment comment, User user);
    long countByComment(Comment comment);
    boolean existsByCommentAndUser(Comment comment, User user);
    void deleteByCommentAndUser(Comment comment, User user);
}
