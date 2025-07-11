package com.example.forum.service.search;

import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.image.ImageDTO;
import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.dto.profile.ProfilePreviewDTO;
import com.example.forum.dto.search.SearchResponseDTO;
import com.example.forum.dto.user.UserDTO;
import com.example.forum.mapper.community.CommunityMapper;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.mapper.profile.ProfileMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityRepository;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.repository.user.UserRepository;
import com.example.forum.service.post.hidden.HiddenPostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SearchServiceImplTest {

    @Mock private PostRepository postRepository;
    @Mock private CommunityRepository communityRepository;
    @Mock private UserRepository userRepository;
    @Mock private HiddenPostService hiddenPostService;

    @InjectMocks
    private SearchServiceImpl searchService;

    private ImageDTO imageDTO;
    private UserDTO authorDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        imageDTO = ImageDTO.builder()
                .imageUrl("http://example.com/image.png")
                .imagePositionX(0.0)
                .imagePositionY(0.0)
                .build();

        authorDTO = UserDTO.builder()
                .username("user1")
                .nickname("nickname1")
                .profileImage(imageDTO)
                .build();
    }

    @Test
    @DisplayName("Success - searchAll returns post, community, user results")
    void success_search_all() {
        String keyword = "test";
        Post post = mock(Post.class);
        Community community = mock(Community.class);
        User user = mock(User.class);

        when(postRepository.findTop5ByTitleContainingIgnoreCase(keyword)).thenReturn(List.of(post));
        when(communityRepository.findTop5ByNameContainingIgnoreCase(keyword)).thenReturn(List.of(community));
        when(userRepository.findTop5ByUsernameContainingIgnoreCase(keyword)).thenReturn(List.of(user));

        PostPreviewDTO postDTO = PostPreviewDTO.builder()
                .id(1L).title("Title").author(authorDTO).build();
        CommunityPreviewDTO communityDTO = CommunityPreviewDTO.builder()
                .id(1L).name("Community").imageDTO(imageDTO).build();
        ProfilePreviewDTO profileDTO = ProfilePreviewDTO.builder()
                .username("user1").nickname("nickname1").imageDto(imageDTO).build();

        try (
                MockedStatic<PostMapper> postMapper = mockStatic(PostMapper.class);
                MockedStatic<CommunityMapper> communityMapper = mockStatic(CommunityMapper.class);
                MockedStatic<ProfileMapper> profileMapper = mockStatic(ProfileMapper.class)
        ) {
            postMapper.when(() -> PostMapper.toPreviewDTO(post, false)).thenReturn(postDTO);
            communityMapper.when(() -> CommunityMapper.toPreviewDTO(community)).thenReturn(communityDTO);
            profileMapper.when(() -> ProfileMapper.toProfilePreviewDTO(user)).thenReturn(profileDTO);

            SearchResponseDTO result = searchService.searchAll(keyword, "test");

            assertThat(result.getPosts()).hasSize(1);
            assertThat(result.getCommunities()).hasSize(1);
            assertThat(result.getUsers()).hasSize(1);

            verify(postRepository).findTop5ByTitleContainingIgnoreCase(keyword);
            verify(communityRepository).findTop5ByNameContainingIgnoreCase(keyword);
            verify(userRepository).findTop5ByUsernameContainingIgnoreCase(keyword);
        }
    }

    @Test
    @DisplayName("Success - searchUsers returns profile previews")
    void success_search_users() {
        String keyword = "nick";
        User user = mock(User.class);
        ProfilePreviewDTO profileDTO = ProfilePreviewDTO.builder()
                .username("user1").nickname("nick").imageDto(imageDTO).build();

        when(userRepository.findTop5ByUsernameContainingIgnoreCase(keyword)).thenReturn(List.of(user));

        try (MockedStatic<ProfileMapper> mapper = mockStatic(ProfileMapper.class)) {
            mapper.when(() -> ProfileMapper.toProfilePreviewDTO(user)).thenReturn(profileDTO);

            List<ProfilePreviewDTO> result = searchService.searchUsers(keyword);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNickname()).isEqualTo("nick");
        }
    }

    @Test
    @DisplayName("Success - searchPosts returns post previews")
    void success_search_posts() {
        String keyword = "post";
        Post post = mock(Post.class);
        PostPreviewDTO postDTO = PostPreviewDTO.builder().id(1L).title("post title").author(authorDTO).build();

        when(postRepository.findTop5ByTitleContainingIgnoreCase(keyword)).thenReturn(List.of(post));

        try (MockedStatic<PostMapper> mapper = mockStatic(PostMapper.class)) {
            mapper.when(() -> PostMapper.toPreviewDTO(post, false)).thenReturn(postDTO);

            List<PostPreviewDTO> result = searchService.searchPosts(keyword,"test");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).contains("post");
        }
    }

    @Test
    @DisplayName("Success - searchCommunities returns community previews")
    void success_search_communities() {
        String keyword = "comm";
        Community community = mock(Community.class);
        CommunityPreviewDTO communityDTO = CommunityPreviewDTO.builder().id(1L).name("comm name").imageDTO(imageDTO).build();

        when(communityRepository.findTop5ByNameContainingIgnoreCase(keyword)).thenReturn(List.of(community));

        try (MockedStatic<CommunityMapper> mapper = mockStatic(CommunityMapper.class)) {
            mapper.when(() -> CommunityMapper.toPreviewDTO(community)).thenReturn(communityDTO);

            List<CommunityPreviewDTO> result = searchService.searchCommunities(keyword);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).contains("comm");
        }
    }
}
