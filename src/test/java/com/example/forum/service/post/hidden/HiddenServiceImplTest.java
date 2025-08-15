package com.example.forum.service.post.hidden;

import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import com.example.forum.repository.post.HiddenPostRepository;
import com.example.forum.validator.auth.AuthValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for HiddenServiceImpl.
 * - Display names and comments are in English.
 * - Each test stubs only what it uses to avoid UnnecessaryStubbingException.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("HiddenServiceImpl")
class HiddenServiceImplTest {

    @InjectMocks
    private HiddenServiceImpl service;

    @Mock private AuthValidator authValidator;
    @Mock private HiddenPostRepository hiddenPostRepository;

    // ---------------------------
    // isHiddenByUsername
    // ---------------------------
    @Nested
    @DisplayName("isHiddenByUsername")
    class IsHiddenByUsername {

        @Test
        @DisplayName("Should return false and skip dependencies when username is null")
        void returnsFalse_whenUsernameNull() {
            Post post = mock(Post.class);

            boolean result = service.isHiddenByUsername(post, null);

            assertFalse(result);
            verifyNoInteractions(authValidator, hiddenPostRepository);
        }

        @Test
        @DisplayName("Should return true when repository says the post is hidden by user")
        void returnsTrue_whenRepoExistsTrue() {
            Post post = mock(Post.class);
            String username = "alice";
            User user = mock(User.class);

            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(hiddenPostRepository.existsByUserAndPost(user, post)).thenReturn(true);

            boolean result = service.isHiddenByUsername(post, username);

            assertTrue(result);
            verify(authValidator, times(1)).validateUserByUsername(username);
            verify(hiddenPostRepository, times(1)).existsByUserAndPost(user, post);
        }

        @Test
        @DisplayName("Should return false when repository says it is not hidden")
        void returnsFalse_whenRepoExistsFalse() {
            Post post = mock(Post.class);
            String username = "bob";
            User user = mock(User.class);

            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(hiddenPostRepository.existsByUserAndPost(user, post)).thenReturn(false);

            boolean result = service.isHiddenByUsername(post, username);

            assertFalse(result);
            verify(authValidator, times(1)).validateUserByUsername(username);
            verify(hiddenPostRepository, times(1)).existsByUserAndPost(user, post);
        }
    }

    // ---------------------------
    // getHiddenPostIdsByUsername
    // ---------------------------
    @Nested
    @DisplayName("getHiddenPostIdsByUsername")
    class GetHiddenPostIdsByUsername {

        @Test
        @DisplayName("Should return empty set and skip dependencies when username is null")
        void returnsEmpty_whenUsernameNull() {
            Set<Long> result = service.getHiddenPostIdsByUsername(null);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verifyNoInteractions(authValidator, hiddenPostRepository);
        }

        @Test
        @DisplayName("Should return a set of IDs from repository list (deduplicated)")
        void returnsSetFromRepository() {
            String username = "alice";
            User user = mock(User.class);

            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            // Repository returns list; service converts to HashSet
            when(hiddenPostRepository.findHiddenPostIdsByUser(user))
                    .thenReturn(List.of(1L, 2L, 2L, 3L));

            Set<Long> result = service.getHiddenPostIdsByUsername(username);

            assertEquals(Set.of(1L, 2L, 3L), result);
            verify(authValidator, times(1)).validateUserByUsername(username);
            verify(hiddenPostRepository, times(1)).findHiddenPostIdsByUser(user);
        }

        @Test
        @DisplayName("Should return empty set when repository returns empty list")
        void returnsEmpty_whenRepositoryEmpty() {
            String username = "carol";
            User user = mock(User.class);

            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(hiddenPostRepository.findHiddenPostIdsByUser(user)).thenReturn(List.of());

            Set<Long> result = service.getHiddenPostIdsByUsername(username);

            assertTrue(result.isEmpty());
            verify(authValidator, times(1)).validateUserByUsername(username);
            verify(hiddenPostRepository, times(1)).findHiddenPostIdsByUser(user);
        }
    }
}
