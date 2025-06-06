package com.example.forum.service.search;

import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.dto.search.SearchResponseDTO;
import com.example.forum.dto.util.AuthorDTO;
import com.example.forum.dto.util.ImageDTO;
import com.example.forum.mapper.community.CommunityMapper;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.repository.community.CommunityRepository;
import com.example.forum.repository.post.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SearchServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommunityRepository communityRepository;

    @InjectMocks
    private SearchServiceImpl searchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Success - Search posts and communities by keyword")
    void success_search_all() {
        // given
        String keyword = "test";

        Post post = mock(Post.class);
        Community community = mock(Community.class);

        List<Post> posts = List.of(post);
        List<Community> communities = List.of(community);

        when(postRepository.findTop5ByTitleContainingIgnoreCase(keyword)).thenReturn(posts);
        when(communityRepository.findTop5ByNameContainingIgnoreCase(keyword)).thenReturn(communities);

        ImageDTO imageDTO = ImageDTO.builder()
                .imageUrl("http://example.com/image.png")
                .imagePositionX(0.0)
                .imagePositionY(0.0)
                .build();

        AuthorDTO authorDTO = AuthorDTO.builder()
                .username("user1")
                .nickname("nickname1")
                .imageDTO(imageDTO)
                .build();

        PostPreviewDTO postPreviewDTO = PostPreviewDTO.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .thumbnailUrls(List.of("http://example.com/image.png"))
                .likeCount(5)
                .commentCount(3)
                .createdAtFormatted("1 hour ago")
                .communityId(1L)
                .communityName("Community 1")
                .communityProfilePicture(imageDTO)
                .authorNickname("nickname1")
                .author(authorDTO)
                .build();

        CommunityPreviewDTO communityPreviewDTO = CommunityPreviewDTO.builder()
                .id(1L)
                .name("Community 1")
                .imageDTO(imageDTO)
                .build();

        mockStatic(PostMapper.class).when(() -> PostMapper.toPreviewDTO(post)).thenReturn(postPreviewDTO);
        mockStatic(CommunityMapper.class).when(() -> CommunityMapper.toPreviewDTO(community)).thenReturn(communityPreviewDTO);

        // when
        SearchResponseDTO result = searchService.searchAll(keyword);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPosts()).hasSize(1);
        assertThat(result.getCommunities()).hasSize(1);

        PostPreviewDTO resultPost = result.getPosts().get(0);
        assertThat(resultPost.getTitle()).isEqualTo("Test Title");
        assertThat(resultPost.getCommunityName()).isEqualTo("Community 1");

        CommunityPreviewDTO resultCommunity = result.getCommunities().get(0);
        assertThat(resultCommunity.getName()).isEqualTo("Community 1");

        // verify
        verify(postRepository).findTop5ByTitleContainingIgnoreCase(keyword);
        verify(communityRepository).findTop5ByNameContainingIgnoreCase(keyword);
    }
}
