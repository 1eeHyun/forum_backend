package com.example.forum.service.community.member;

import com.example.forum.dto.user.UserDTO;
import com.example.forum.exception.auth.ForbiddenException;
import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityMember;
import com.example.forum.model.community.CommunityRole;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityMemberRepository;
import com.example.forum.service.auth.RedisService;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CommunityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommunityMemberService Tests")
class CommunityMemberServiceImplTest {

    @InjectMocks
    private CommunityMemberServiceImpl communityMemberService;

    @Mock private AuthValidator authValidator;
    @Mock private CommunityValidator communityValidator;
    @Mock private CommunityMemberRepository communityMemberRepository;
    @Mock private RedisService redisService;

    private final Long communityId = 1L;
    private Community community;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(10L).username("testuser").build();
        community = Community.builder().id(communityId).members(new HashSet<>()).build();
    }

    @Test
    @DisplayName("getOnlineUsers - returns only users marked online in Redis")
    void getOnlineUsers_success() {
        CommunityMember cm1 = CommunityMember.builder().user(user).build();
        CommunityMember cm2 = CommunityMember.builder().user(User.builder().id(11L).build()).build();

        when(communityValidator.validateExistingCommunity(communityId)).thenReturn(community);
        when(communityMemberRepository.findByCommunity(community)).thenReturn(List.of(cm1, cm2));
        when(redisService.isUserOnline(10L)).thenReturn(true);
        when(redisService.isUserOnline(11L)).thenReturn(false);

        List<UserDTO> result = communityMemberService.getOnlineUsers(communityId);

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    @Test
    @DisplayName("addMember - adds user when not already a member")
    void addMember_success() {
        when(communityValidator.validateExistingCommunity(communityId)).thenReturn(community);
        when(authValidator.validateUserByUsername("testuser")).thenReturn(user);

        communityMemberService.addMember(communityId, "testuser");

        assertEquals(1, community.getMembers().size());
        assertTrue(community.getMembers().stream().anyMatch(cm -> cm.getUser().equals(user)));
    }

    @Test
    @DisplayName("addMember - does not add duplicate if user is already a member")
    void addMember_shouldNotDuplicateIfAlreadyMember() {
        community.addMember(user, CommunityRole.MEMBER);
        int beforeSize = community.getMembers().size();

        when(communityValidator.validateExistingCommunity(communityId)).thenReturn(community);
        when(authValidator.validateUserByUsername("testuser")).thenReturn(user);

        communityMemberService.addMember(communityId, "testuser");

        assertEquals(beforeSize, community.getMembers().size());
    }

    @Test
    @DisplayName("leaveCommunity - successfully removes member if exists")
    void leaveCommunity_shouldSucceedIfExists() {
        community.addMember(user, CommunityRole.MEMBER);

        when(communityValidator.validateMemberCommunity(communityId, user)).thenReturn(community);
        when(authValidator.validateUserByUsername("testuser")).thenReturn(user);

        communityMemberService.leaveCommunity(communityId, "testuser");

        assertEquals(0, community.getMembers().size());
    }

    @Test
    @DisplayName("leaveCommunity - throws if user is not a member")
    void leaveCommunity_shouldThrowIfNotMember() {
        when(authValidator.validateUserByUsername("testuser")).thenReturn(user);
        when(communityValidator.validateMemberCommunity(communityId, user)).thenReturn(community);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> communityMemberService.leaveCommunity(communityId, "testuser"));

        assertEquals("Membership record not found", ex.getMessage());
    }


    @Test
    @DisplayName("leaveCommunity - manager cannot leave the community")
    void leaveCommunity_shouldFailIfUserIsManager() {
        community.addMember(user, CommunityRole.MANAGER);

        when(authValidator.validateUserByUsername("testuser")).thenReturn(user);
        when(communityValidator.validateMemberCommunity(communityId, user)).thenReturn(community);

        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> communityMemberService.leaveCommunity(communityId, "testuser"));

        assertEquals("Managers cannot leave the community directly.", ex.getMessage());
        assertEquals(1, community.getMembers().size());
    }

    @Test
    @DisplayName("getNewMembersThisWeek - returns members joined within 7 days")
    void getNewMembersThisWeek_success() {
        LocalDateTime now = LocalDateTime.now();
        CommunityMember recent = CommunityMember.builder()
                .user(user)
                .joinedAt(now.minusDays(3))
                .build();

        when(communityValidator.validateExistingCommunity(communityId)).thenReturn(community);
        when(communityMemberRepository.findByCommunityAndJoinedAtAfter(eq(community), any()))
                .thenReturn(List.of(recent));

        List<UserDTO> result = communityMemberService.getNewMembersThisWeek(communityId);

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    @Test
    @DisplayName("getAllMembers - returns members only if user is a manager")
    void getAllMembers_success() {
        CommunityMember cm = CommunityMember.builder().user(user).build();
        community.getMembers().add(cm);

        when(authValidator.validateUserByUsername("testuser")).thenReturn(user);
        when(communityValidator.validateExistingCommunity(communityId)).thenReturn(community);
        doNothing().when(communityValidator).validateManagerPermission(user, community);

        List<UserDTO> result = communityMemberService.getAllMembers(communityId, "testuser");

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }
}
