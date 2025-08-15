package com.example.forum.service.post;

import com.example.forum.common.SortOrder;
import com.example.forum.dto.post.PostCreateRequestDTO;
import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostFileDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.community.Category;
import com.example.forum.model.community.Community;
import com.example.forum.model.post.HiddenPost;
import com.example.forum.model.post.Post;
import com.example.forum.model.post.PostFile;
import com.example.forum.model.post.Visibility;
import com.example.forum.model.user.User;
import com.example.forum.repository.bookmark.BookmarkRepository;
import com.example.forum.repository.community.CommunityFavoriteRepository;
import com.example.forum.repository.like.PostReactionRepository;
import com.example.forum.repository.post.HiddenPostRepository;
import com.example.forum.repository.post.PostQueryRepository;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.service.common.RecentViewService;
import com.example.forum.service.common.S3Service;
import com.example.forum.service.post.hidden.HiddenPostService;
import com.example.forum.service.tag.TaggingService;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CategoryValidator;
import com.example.forum.validator.community.CommunityValidator;
import com.example.forum.validator.post.PostValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PostServiceImpl.
 * - Display names and comments are in English.
 * - Focuses on collaborator calls, flags, and branching logic.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PostServiceImpl")
class PostServiceImplTest {

    @InjectMocks
    private PostServiceImpl service;

    // Validators
    @Mock private AuthValidator authValidator;
    @Mock private PostValidator postValidator;
    @Mock private CategoryValidator categoryValidator;
    @Mock private CommunityValidator communityValidator;

    // Repositories
    @Mock private PostRepository postRepository;
    @Mock private HiddenPostRepository hiddenPostRepository;
    @Mock private CommunityFavoriteRepository communityFavoriteRepository;
    @Mock private BookmarkRepository bookmarkRepository;
    @Mock private PostReactionRepository postReactionRepository;
    @Mock private PostQueryRepository postQueryRepository;

    // Services
    @Mock private S3Service s3Service;
    @Mock private RecentViewService recentViewService;
    @Mock private TaggingService taggingService;
    @Mock private HiddenPostService hiddenPostService;

    // ---------------------------
    // getPagedPosts
    // ---------------------------
    @Nested
    @DisplayName("getPagedPosts")
    class GetPagedPosts {

        @Test
        @DisplayName("Should map NEWEST posts with hidden/favorite flags when username is provided")
        void newest_withUser_flagsApplied() {
            String username = "alice";
            User user = mock(User.class);
            when(authValidator.validateUserByUsername(username)).thenReturn(user);

            // Posts
            Post p1 = mock(Post.class); when(p1.getId()).thenReturn(1L);
            Post p2 = mock(Post.class); when(p2.getId()).thenReturn(2L);

            // Categories & communities
            Community cFav = mock(Community.class); when(cFav.getId()).thenReturn(100L);
            Category cat1 = mock(Category.class); when(cat1.getCommunity()).thenReturn(cFav);
            when(p1.getCategory()).thenReturn(cat1);        // p1 has favorite community
            when(p2.getCategory()).thenReturn(null);        // p2 no community

            when(postRepository.findPagedPostsNewest(10, 0)).thenReturn(List.of(p1, p2));

            // Hidden: only id=2 hidden
            when(hiddenPostService.getHiddenPostIdsByUsername(username)).thenReturn(Set.of(2L));

            // Favorites: user has favorite community=100
            var favEntity = mock(com.example.forum.model.community.CommunityFavorite.class);
            when(favEntity.getCommunity()).thenReturn(cFav);
            when(communityFavoriteRepository.findAllByUser(user)).thenReturn(List.of(favEntity));

            try (MockedStatic<PostMapper> ms = mockStatic(PostMapper.class)) {
                PostResponseDTO d1 = mock(PostResponseDTO.class);
                PostResponseDTO d2 = mock(PostResponseDTO.class);

                // p1 -> hidden=false, favorite=true
                ms.when(() -> PostMapper.toPostResponseDTO(eq(p1), eq(false), eq(true))).thenReturn(d1);
                // p2 -> hidden=true, favorite=false
                ms.when(() -> PostMapper.toPostResponseDTO(eq(p2), eq(true), eq(false))).thenReturn(d2);

                List<PostResponseDTO> result = service.getPagedPosts(SortOrder.NEWEST, 0, 10, username);
                assertEquals(List.of(d1, d2), result);
            }
        }

        @Test
        @DisplayName("Should delegate TOP_LIKED and work without username (no favorites)")
        void topLiked_withoutUser() {
            // username null -> authValidator not called; favorite ids empty
            Post p = mock(Post.class); when(p.getId()).thenReturn(9L);
            when(postRepository.findPagedPostsTopLiked(5, 5)).thenReturn(List.of(p));

            when(hiddenPostService.getHiddenPostIdsByUsername(null)).thenReturn(Set.of());

            try (MockedStatic<PostMapper> ms = mockStatic(PostMapper.class)) {
                PostResponseDTO dto = mock(PostResponseDTO.class);
                ms.when(() -> PostMapper.toPostResponseDTO(eq(p), eq(false), eq(false))).thenReturn(dto);

                List<PostResponseDTO> result = service.getPagedPosts(SortOrder.TOP_LIKED, 1, 5, null);
                assertEquals(List.of(dto), result);

                verifyNoInteractions(authValidator, communityFavoriteRepository);
            }
        }
    }

    // ---------------------------
    // getPostDetail
    // ---------------------------
    @Nested
    @DisplayName("getPostDetail")
    class GetPostDetail {

        @Test
        @DisplayName("Should map detail with viewer=null and not touch recentView")
        void viewerNull() {
            Long postId = 10L;
            Post post = mock(Post.class);
            when(postValidator.validateDetailPostId(postId)).thenReturn(post);
            when(hiddenPostService.isHiddenByUsername(post, null)).thenReturn(false);
            when(post.getCategory()).thenReturn(null);

            try (MockedStatic<PostMapper> ms = mockStatic(PostMapper.class)) {
                PostDetailDTO dto = mock(PostDetailDTO.class);
                ms.when(() -> PostMapper.toPostDetailDTO(eq(post), isNull(), eq(false), eq(false))).thenReturn(dto);

                PostDetailDTO result = service.getPostDetail(postId, null);
                assertEquals(dto, result);

                verifyNoInteractions(recentViewService, communityFavoriteRepository, authValidator);
            }
        }

        @Test
        @DisplayName("Should map detail with viewer present, favorite=true, and add recent view")
        void viewerPresent_favoriteTrue_addRecent() {
            Long postId = 11L;
            String username = "alice";
            User viewer = mock(User.class); when(viewer.getId()).thenReturn(77L);

            Post post = mock(Post.class);
            Community community = mock(Community.class);
            Category category = mock(Category.class);
            when(category.getCommunity()).thenReturn(community);
            when(post.getCategory()).thenReturn(category);

            when(authValidator.validateUserByUsername(username)).thenReturn(viewer);
            when(postValidator.validateDetailPostId(postId)).thenReturn(post);
            when(hiddenPostService.isHiddenByUsername(post, username)).thenReturn(true);
            when(communityFavoriteRepository.existsByUserAndCommunity(viewer, community)).thenReturn(true);

            try (MockedStatic<PostMapper> ms = mockStatic(PostMapper.class)) {
                PostDetailDTO dto = mock(PostDetailDTO.class);
                ms.when(() -> PostMapper.toPostDetailDTO(eq(post), eq(viewer), eq(true), eq(true))).thenReturn(dto);

                PostDetailDTO result = service.getPostDetail(postId, username);
                assertEquals(dto, result);

                verify(recentViewService).addPostView(77L, postId);
            }
        }
    }

    // ---------------------------
    // createPost
    // ---------------------------
    @Nested
    @DisplayName("createPost")
    class CreatePost {

        @Test
        @DisplayName("Should create PUBLIC post and set tags (isPublicWithTagsCheckingDTO)")
        void publicPost_setsTags() {
            String username = "alice";
            User user = mock(User.class);
            when(authValidator.validateUserByUsername(username)).thenReturn(user);

            // DTO mock (PUBLIC + tags + files)
            PostCreateRequestDTO dto = mock(PostCreateRequestDTO.class);
            when(dto.getVisibility()).thenReturn(Visibility.PUBLIC);
            when(dto.getTitle()).thenReturn("t");
            when(dto.getContent()).thenReturn("c");
            when(dto.getFileUrls()).thenReturn(List.of()); // no files
            when(dto.getTags()).thenReturn(List.of("java", "spring"));

            // validate count
            // (no additional stubs needed)
            // save
            Post saved = mock(Post.class);
            when(saved.getId()).thenReturn(123L);
            // repository.save(Post) returns saved
            when(postRepository.save(any(Post.class))).thenReturn(saved);

            // query with tags returns empty => fallback to saved
            when(postQueryRepository.findByIdWithTags(123L)).thenReturn(Optional.empty());

            try (MockedStatic<PostMapper> ms = mockStatic(PostMapper.class)) {
                PostResponseDTO resp = mock(PostResponseDTO.class);
                ms.when(() -> PostMapper.toPostResponseDTO(eq(saved), eq(false), eq(false))).thenReturn(resp);

                PostResponseDTO result = service.createPost(dto, username);
                assertEquals(resp, result);

                // tag validations for PUBLIC
                verify(postValidator).isPublicWithTagsCheckingDTO(dto);
                verify(taggingService).setTagsForPost(123L, List.of("java", "spring"));
                verifyNoInteractions(communityValidator, categoryValidator);
            }
        }

        @Test
        @DisplayName("Should create COMMUNITY post, validate category & membership, no tags call if not PUBLIC")
        void communityPost_validatesMembership_noTags() {
            String username = "bob";
            User user = mock(User.class);
            when(authValidator.validateUserByUsername(username)).thenReturn(user);

            // DTO mock (COMMUNITY)
            PostCreateRequestDTO dto = mock(PostCreateRequestDTO.class);
            when(dto.getVisibility()).thenReturn(Visibility.COMMUNITY);
            when(dto.getTitle()).thenReturn("t");
            when(dto.getContent()).thenReturn("c");
            when(dto.getCategoryId()).thenReturn(77L);
            when(dto.getFileUrls()).thenReturn(List.of());

            // Category & community
            Category category = mock(Category.class);
            Community community = mock(Community.class); when(community.getId()).thenReturn(500L);
            when(category.getCommunity()).thenReturn(community);
            when(categoryValidator.validateCategoryById(77L)).thenReturn(category);

            // Save
            Post saved = mock(Post.class); when(saved.getId()).thenReturn(999L);
            when(postRepository.save(any(Post.class))).thenReturn(saved);
            when(postQueryRepository.findByIdWithTags(999L)).thenReturn(Optional.of(saved));

            try (MockedStatic<PostMapper> ms = mockStatic(PostMapper.class)) {
                PostResponseDTO resp = mock(PostResponseDTO.class);
                ms.when(() -> PostMapper.toPostResponseDTO(eq(saved), eq(false), eq(false))).thenReturn(resp);

                PostResponseDTO result = service.createPost(dto, username);
                assertEquals(resp, result);

                verify(communityValidator).validateMemberCommunity(500L, user);
                verify(taggingService, never()).setTagsForPost(anyLong(), anyList());
            }
        }
    }

    // ---------------------------
    // updatePost
    // ---------------------------
    @Nested
    @DisplayName("updatePost")
    class UpdatePost {

        @Test
        @DisplayName("Should delete old files from S3, update fields and clear tags when PUBLIC -> PRIVATE")
        void publicToPrivate_clearsTags_andDeletesS3() {
            Long postId = 1L;
            String username = "alice";
            User user = mock(User.class);
            when(authValidator.validateUserByUsername(username)).thenReturn(user);

            // Existing post (mock with files, PUBLIC)
            Post post = mock(Post.class);
            when(post.getVisibility()).thenReturn(Visibility.PUBLIC);
            // existing files
            PostFile f1 = mock(PostFile.class); when(f1.getFileUrl()).thenReturn("old1");
            PostFile f2 = mock(PostFile.class); when(f2.getFileUrl()).thenReturn("old2");
            // post.getFiles() returns a mutable list to be cleared
            List<PostFile> files = new ArrayList<>(List.of(f1, f2));
            when(post.getFiles()).thenReturn(files);

            when(postValidator.validatePost(postId)).thenReturn(post);
            doNothing().when(postValidator).validatePostAuthor(post, user);
            doNothing().when(postValidator).validatePostCount(any());

            // DTO: switch to PRIVATE, new files two items (we won't verify builder-created PostFile content)
            PostCreateRequestDTO dto = mock(PostCreateRequestDTO.class);
            when(dto.getVisibility()).thenReturn(Visibility.PRIVATE);
            when(dto.getTitle()).thenReturn("newTitle");
            when(dto.getContent()).thenReturn("newContent");
            when(dto.getFileUrls()).thenReturn(List.of(
                    new PostFileDTO("new1", null),
                    new PostFileDTO("new2", null)
            ));

            // Save returns same post id 1
            Post saved = mock(Post.class); when(saved.getId()).thenReturn(1L);
            when(postRepository.save(post)).thenReturn(saved);

            // No tags loaded from query repo
            when(postQueryRepository.findByIdWithTags(1L)).thenReturn(Optional.empty());

            // Favorite check -> false
            when(saved.getCategory()).thenReturn(null);

            try (MockedStatic<PostMapper> ms = mockStatic(PostMapper.class)) {
                PostResponseDTO resp = mock(PostResponseDTO.class);
                ms.when(() -> PostMapper.toPostResponseDTO(eq(saved), eq(false), eq(false))).thenReturn(resp);

                PostResponseDTO result = service.updatePost(postId, dto, username);
                assertEquals(resp, result);

                // Old S3 files must be deleted
                verify(s3Service).deleteFiles(List.of("old1", "old2"));
                // PUBLIC -> non-PUBLIC => clear all tags
                verify(taggingService).setTagsForPost(1L, List.of());
            }
        }

        @Test
        @DisplayName("Should keep/optionally set tags when visibility is PUBLIC and tags provided")
        void staysPublic_setsTagsIfProvided() {
            Long postId = 2L;
            String username = "alice";
            User user = mock(User.class);
            when(authValidator.validateUserByUsername(username)).thenReturn(user);

            Post post = mock(Post.class);
            when(post.getVisibility()).thenReturn(Visibility.PRIVATE); // old PRIVATE

            // No old files
            when(post.getFiles()).thenReturn(null);
            when(postValidator.validatePost(postId)).thenReturn(post);
            doNothing().when(postValidator).validatePostAuthor(post, user);
            doNothing().when(postValidator).validatePostCount(any());

            // DTO: new PUBLIC with tags
            PostCreateRequestDTO dto = mock(PostCreateRequestDTO.class);
            when(dto.getVisibility()).thenReturn(Visibility.PUBLIC);
            when(dto.getTitle()).thenReturn("t");
            when(dto.getContent()).thenReturn("c");
            when(dto.getTags()).thenReturn(List.of("tag"));
            when(dto.getFileUrls()).thenReturn(List.of());

            // Save + query with tags returns 'withTags' (category->community favorite true)
            Post saved = mock(Post.class); when(saved.getId()).thenReturn(2L);
            when(postRepository.save(post)).thenReturn(saved);

            Post withTags = mock(Post.class);
            Category cat = mock(Category.class);
            Community community = mock(Community.class);
            when(cat.getCommunity()).thenReturn(community);
            when(withTags.getCategory()).thenReturn(cat);
            when(postQueryRepository.findByIdWithTags(2L)).thenReturn(Optional.of(withTags));

            when(communityFavoriteRepository.existsByUserAndCommunity(user, community)).thenReturn(true);

            try (MockedStatic<PostMapper> ms = mockStatic(PostMapper.class)) {
                PostResponseDTO resp = mock(PostResponseDTO.class);
                ms.when(() -> PostMapper.toPostResponseDTO(eq(withTags), eq(false), eq(true))).thenReturn(resp);

                PostResponseDTO result = service.updatePost(postId, dto, username);
                assertEquals(resp, result);

                // PUBLIC with non-null tags => set tags called
                verify(taggingService).setTagsForPost(2L, List.of("tag"));
            }
        }

        @Test
        @DisplayName("Should NOT touch tags when visibility is PUBLIC but tags are null")
        void publicWithNullTags_doesNotSetTags() {
            Long postId = 3L;
            String username = "alice";
            User user = mock(User.class);
            when(authValidator.validateUserByUsername(username)).thenReturn(user);

            Post post = mock(Post.class);
            when(post.getVisibility()).thenReturn(Visibility.PRIVATE);
            when(post.getFiles()).thenReturn(null);

            when(postValidator.validatePost(postId)).thenReturn(post);
            doNothing().when(postValidator).validatePostAuthor(post, user);
            doNothing().when(postValidator).validatePostCount(any());

            PostCreateRequestDTO dto = mock(PostCreateRequestDTO.class);
            when(dto.getVisibility()).thenReturn(Visibility.PUBLIC);
            when(dto.getTitle()).thenReturn("t");
            when(dto.getContent()).thenReturn("c");
            when(dto.getTags()).thenReturn(null); // null tags
            when(dto.getFileUrls()).thenReturn(List.of());

            Post saved = mock(Post.class); when(saved.getId()).thenReturn(3L);
            when(postRepository.save(post)).thenReturn(saved);

            when(postQueryRepository.findByIdWithTags(3L)).thenReturn(Optional.of(saved));
            when(saved.getCategory()).thenReturn(null);

            try (MockedStatic<PostMapper> ms = mockStatic(PostMapper.class)) {
                PostResponseDTO resp = mock(PostResponseDTO.class);
                ms.when(() -> PostMapper.toPostResponseDTO(eq(saved), eq(false), eq(false))).thenReturn(resp);

                PostResponseDTO result = service.updatePost(postId, dto, username);
                assertEquals(resp, result);

                verify(taggingService, never()).setTagsForPost(anyLong(), anyList());
            }
        }
    }

    // ---------------------------
    // deletePost
    // ---------------------------
    @Test
    @DisplayName("deletePost should validate author, delete children then parent")
    void deletePost_deletesChildrenThenParent() {
        Long postId = 10L;
        String username = "alice";
        User user = mock(User.class);
        Post post = mock(Post.class);

        when(authValidator.validateUserByUsername(username)).thenReturn(user);
        when(postValidator.validatePost(postId)).thenReturn(post);
        doNothing().when(postValidator).validatePostAuthor(post, user);

        service.deletePost(postId, username);

        InOrder inOrder = inOrder(bookmarkRepository, postReactionRepository, postRepository);
        inOrder.verify(bookmarkRepository).deleteByPostId(postId);
        inOrder.verify(postReactionRepository).deleteByPostId(postId);
        inOrder.verify(postRepository).delete(post);
    }

    // ---------------------------
    // toggleHidePost
    // ---------------------------
    @Nested
    @DisplayName("toggleHidePost")
    class ToggleHidePost {

        @Test
        @DisplayName("Should delete when hidden exists")
        void deleteExisting() {
            Long postId = 1L;
            String username = "alice";
            User user = mock(User.class);
            Post post = mock(Post.class);
            HiddenPost hp = mock(HiddenPost.class);

            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(postValidator.validatePost(postId)).thenReturn(post);
            when(hiddenPostRepository.findByUserAndPost(user, post)).thenReturn(Optional.of(hp));

            service.toggleHidePost(postId, username);

            verify(hiddenPostRepository).delete(hp);
            verify(hiddenPostRepository, never()).save(any(HiddenPost.class));
        }

        @Test
        @DisplayName("Should save when hidden does not exist")
        void saveNew() {
            Long postId = 2L;
            String username = "bob";
            User user = mock(User.class);
            Post post = mock(Post.class);

            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(postValidator.validatePost(postId)).thenReturn(post);
            when(hiddenPostRepository.findByUserAndPost(user, post)).thenReturn(Optional.empty());

            service.toggleHidePost(postId, username);

            verify(hiddenPostRepository).save(any(HiddenPost.class));
        }
    }

    // ---------------------------
    // getHiddenPostIds
    // ---------------------------
    @Test
    @DisplayName("getHiddenPostIds should delegate to repository with validated user")
    void getHiddenPostIds_delegates() {
        String username = "alice";
        User user = mock(User.class);
        when(authValidator.validateUserByUsername(username)).thenReturn(user);
        when(hiddenPostRepository.findHiddenPostIdsByUser(user)).thenReturn(List.of(1L, 2L));

        List<Long> ids = service.getHiddenPostIds(username);
        assertEquals(List.of(1L, 2L), ids);
    }
}
