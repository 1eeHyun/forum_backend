package com.example.forum.service.bookmark;

import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.bookmark.Bookmark;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import com.example.forum.repository.bookmark.BookmarkRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    private final PostValidator postValidator;
    private final AuthValidator userValidator;

    @Override
    public void toggleBookmark(Long postId, String username) {

        Post post = postValidator.validatePost(postId);
        User user = userValidator.validateUserByUsername(username);

        bookmarkRepository.findByUserAndPost(user, post)
                .ifPresentOrElse(
                        bookmarkRepository::delete,
                        () -> bookmarkRepository.save(new Bookmark(null, user, post))
                );
    }

    @Override
    public boolean isBookmarked(Long postId, String username) {

        Post post = postValidator.validatePost(postId);
        User user = userValidator.validateUserByUsername(username);

        return bookmarkRepository.existsByUserAndPost(user, post);
    }

    @Override
    public List<PostPreviewDTO> getBookmarkedPosts(String username) {

        User user = userValidator.validateUserByUsername(username);

        return bookmarkRepository.findAllByUser(user).stream()
                .map(bookmark -> PostMapper.toPreviewDTO(bookmark.getPost()))
                .toList();
    }
}
