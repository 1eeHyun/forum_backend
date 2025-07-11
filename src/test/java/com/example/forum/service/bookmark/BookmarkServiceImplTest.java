package com.example.forum.service.bookmark;

import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.model.bookmark.Bookmark;
import com.example.forum.model.post.Post;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import com.example.forum.repository.bookmark.BookmarkRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.post.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookmarkServiceImplTest {

    @InjectMocks
    private BookmarkServiceImpl bookmarkService;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private PostValidator postValidator;

    @Mock
    private AuthValidator authValidator;

    @Captor
    ArgumentCaptor<Bookmark> bookmarkCaptor;

    private final String username = "john_doe";
    private User mockUser;
    private Post mockPost;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = User.builder().id(1L).username(username).build();

        User user = User.builder()
                .username("testUser")
                .profile(Profile.builder().nickname("tester").build())
                .build();

        Post post = Post.builder()
                .id(1L)
                .title("Bookmark Test Post")
                .content("Some content")
                .likes(List.of())
                .comments(List.of())
                .createdAt(LocalDateTime.now())
                .author(user)
                .build();

        mockPost = post;
    }

    @Nested
    @DisplayName("toggleBookmark()")
    class ToggleBookmark {

        @Test
        @DisplayName("should add bookmark when not bookmarked yet")
        void addBookmark() {
            when(postValidator.validatePost(10L)).thenReturn(mockPost);
            when(authValidator.validateUserByUsername(username)).thenReturn(mockUser);
            when(bookmarkRepository.findByUserAndPost(mockUser, mockPost)).thenReturn(Optional.empty());

            bookmarkService.toggleBookmark(10L, username);

            verify(bookmarkRepository).save(bookmarkCaptor.capture());
            Bookmark saved = bookmarkCaptor.getValue();
            assertThat(saved.getUser()).isEqualTo(mockUser);
            assertThat(saved.getPost()).isEqualTo(mockPost);
        }

        @Test
        @DisplayName("should remove bookmark when already bookmarked")
        void removeBookmark() {
            Bookmark bookmark = new Bookmark(1L, mockUser, mockPost);
            when(postValidator.validatePost(10L)).thenReturn(mockPost);
            when(authValidator.validateUserByUsername(username)).thenReturn(mockUser);
            when(bookmarkRepository.findByUserAndPost(mockUser, mockPost)).thenReturn(Optional.of(bookmark));

            bookmarkService.toggleBookmark(10L, username);

            verify(bookmarkRepository).delete(bookmark);
        }
    }

    @Nested
    @DisplayName("isBookmarked()")
    class IsBookmarked {

        @Test
        @DisplayName("should return true if bookmarked")
        void isBookmarkedTrue() {
            when(postValidator.validatePost(10L)).thenReturn(mockPost);
            when(authValidator.validateUserByUsername(username)).thenReturn(mockUser);
            when(bookmarkRepository.existsByUserAndPost(mockUser, mockPost)).thenReturn(true);

            boolean result = bookmarkService.isBookmarked(10L, username);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("should return false if not bookmarked")
        void isBookmarkedFalse() {
            when(postValidator.validatePost(10L)).thenReturn(mockPost);
            when(authValidator.validateUserByUsername(username)).thenReturn(mockUser);
            when(bookmarkRepository.existsByUserAndPost(mockUser, mockPost)).thenReturn(false);

            boolean result = bookmarkService.isBookmarked(10L, username);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("getBookmarkedPosts()")
    class GetBookmarkedPosts {

        @Test
        @DisplayName("should return list of PostPreviewDTO from bookmarks")
        void returnPostPreviewDTOs() {
            Bookmark bookmark = new Bookmark(1L, mockUser, mockPost);
            when(authValidator.validateUserByUsername(username)).thenReturn(mockUser);
            when(bookmarkRepository.findAllByUser(mockUser)).thenReturn(List.of(bookmark));

            List<PostPreviewDTO> result = bookmarkService.getBookmarkedPosts(username);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(mockPost.getId());
        }
    }
}
