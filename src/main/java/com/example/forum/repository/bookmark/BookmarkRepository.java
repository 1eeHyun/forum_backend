package com.example.forum.repository.bookmark;

import com.example.forum.model.bookmark.Bookmark;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /**
     * Retrieve if user has bookmarked a post*
     */
    Optional<Bookmark> findByUserAndPost(User user, Post post);

    boolean existsByUserAndPost(User user, Post post);
    List<Bookmark> findAllByUser(User user);
    void deleteByUserAndPost(User user, Post post);
}
