package com.example.forum.service.community;

import com.example.forum.dto.community.CommunityRequestDTO;
import com.example.forum.model.community.Community;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CommunityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommunityServiceImpl Unit Tests")
class CommunityServiceImplTest {

    @InjectMocks
    private CommunityServiceImpl communityService;

    @Mock
    private CommunityRepository communityRepository;

    @Mock
    private AuthValidator authValidator;

    @Mock
    private CommunityValidator communityValidator;

    private User mockUser;

    @BeforeEach
    void setUp() {
        Profile profile = Profile.builder()
                .nickname("tester")
                .build();

        mockUser = User.builder()
                .id(1L)
                .username("tester")
                .email("test@example.com")
                .profile(profile)
                .build();
        profile.setUser(mockUser);
    }

    @Test
    void create_shouldReturnCreatedCommunityDTO() {
        CommunityRequestDTO dto = new CommunityRequestDTO("Forum Devs", "All about backend");
        Community savedCommunity = Community.builder()
                .id(1L)
                .name(dto.getName())
                .description(dto.getDescription())
                .creator(mockUser)
                .members(new HashSet<>())
                .build();

        when(authValidator.validateUserByUsername("tester")).thenReturn(mockUser);
        doNothing().when(communityValidator).validateUniqueName(dto.getName());
        when(communityRepository.save(any(Community.class))).thenReturn(savedCommunity);

        var result = communityService.create(dto, "tester");

        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals("tester", result.getAuthor().getNickname());
    }

    @Test
    void getMyCommunities_shouldReturnListOfCommunityDTOs() {
        Community community = Community.builder()
                .id(1L)
                .name("Test Community")
                .description("For testing")
                .creator(mockUser)
                .members(new HashSet<>()) // initialize members
                .build();

        when(authValidator.validateUserByUsername("tester")).thenReturn(mockUser);
        when(communityRepository.findAllByMembersContaining(mockUser)).thenReturn(List.of(community));

        var result = communityService.getMyCommunities("tester");

        assertEquals(1, result.size());
        assertEquals("Test Community", result.get(0).getName());
    }
}
