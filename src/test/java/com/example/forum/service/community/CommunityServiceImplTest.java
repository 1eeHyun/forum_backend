package com.example.forum.service.community;

import com.example.forum.dto.community.CommunityDetailDTO;
import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.community.CommunityRequestDTO;
import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityMember;
import com.example.forum.model.community.CommunityRole;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CategoryRepository;
import com.example.forum.repository.community.CommunityMemberRepository;
import com.example.forum.repository.community.CommunityRepository;
import com.example.forum.repository.community.CommunityRuleRepository;
import com.example.forum.service.auth.RedisService;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CommunityRuleValidator;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommunityServiceImplTest {

    @InjectMocks
    private CommunityServiceImpl communityService;

    @Mock private AuthValidator authValidator;
    @Mock private CommunityValidator communityValidator;
    @Mock private CommunityRuleValidator communityRuleValidator;
    @Mock private CommunityRepository communityRepository;
    @Mock private CommunityMemberRepository communityMemberRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private CommunityRuleRepository communityRuleRepository;
    @Mock private RedisService redisService;

    private User user;
    private Community community;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("tester").build();
        community = Community.builder().id(1L).name("devs").members(new HashSet<>()).build();
    }

    @Test
    @DisplayName("create - creates new community and adds creator as manager")
    void create_success() {
        // given
        CommunityRequestDTO dto = new CommunityRequestDTO("test-name", "desc");
        User creator = User.builder().id(100L).username("testuser").build();

        when(authValidator.validateUserByUsername("testuser")).thenReturn(creator);
        when(communityRepository.save(any(Community.class))).thenAnswer(invocation -> {
            Community c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });
        when(communityMemberRepository.save(any(CommunityMember.class))).thenReturn(null); // or any stub

        // when
        Long communityId = communityService.create(dto, "testuser");

        // then
        assertNotNull(communityId);
        assertEquals(1L, communityId);
    }


    @Test
    @DisplayName("getCommunityDetail - returns detailed community info")
    void getCommunityDetail_success() {
        CommunityMember cm = CommunityMember.builder().user(user).role(CommunityRole.MEMBER).build();
        community.getMembers().add(cm);

        when(communityValidator.validateExistingCommunity(1L)).thenReturn(community);
        when(communityMemberRepository.findByCommunity(community)).thenReturn(List.of(cm));
        when(authValidator.validateUserByUsername("tester")).thenReturn(user);
        when(communityMemberRepository.findByCommunityAndUser(community, user)).thenReturn(Optional.of(cm));

        CommunityDetailDTO result = communityService.getCommunityDetail(1L, "tester");

        assertEquals("devs", result.getName());
        assertEquals(1, result.getMemberCount());
    }

    @Test
    @DisplayName("getMyCommunities - returns communities user is part of")
    void getMyCommunities_success() {
        CommunityMember cm = CommunityMember.builder().user(user).community(community).build();
        when(authValidator.validateUserByUsername("tester")).thenReturn(user);
        when(communityMemberRepository.findByUser(user)).thenReturn(List.of(cm));

        List<CommunityPreviewDTO> result = communityService.getMyCommunities("tester");

        assertEquals(1, result.size());
        assertEquals("devs", result.get(0).getName());
    }

    @Test
    @DisplayName("getCommunityDetail - user not found")
    void getCommunityDetail_userNotFound() {
        when(communityValidator.validateExistingCommunity(1L)).thenReturn(community);
        when(authValidator.validateUserByUsername("nonexistent")).thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class, () -> communityService.getCommunityDetail(1L, "nonexistent"));
    }

    @Test
    @DisplayName("getMyCommunities - user has no communities")
    void getMyCommunities_empty() {
        when(authValidator.validateUserByUsername("tester")).thenReturn(user);
        when(communityMemberRepository.findByUser(user)).thenReturn(List.of());

        List<CommunityPreviewDTO> result = communityService.getMyCommunities("tester");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("create - fails when user not found")
    void create_userNotFound() {
        CommunityRequestDTO dto = new CommunityRequestDTO("devs", "Developers");
        when(authValidator.validateUserByUsername("invalid")).thenThrow(new RuntimeException("Invalid user"));

        assertThrows(RuntimeException.class, () -> communityService.create(dto, "invalid"));
    }
}
