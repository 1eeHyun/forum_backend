package com.example.forum.repository.comment;

import com.example.forum.model.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.replies WHERE c.post.id = :postId AND c.parentComment IS NULL")
    List<Comment> findTopLevelCommentsWithReplies(@Param("postId") Long postId);
}
