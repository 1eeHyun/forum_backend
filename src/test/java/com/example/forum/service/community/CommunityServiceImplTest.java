package com.example.forum.service.community;

import com.example.forum.dto.community.CommunityDetailDTO;
import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.community.CommunityRequestDTO;
import com.example.forum.exception.auth.UserNotFoundException;
import com.example.forum.exception.community.CommunityNotFoundException;
import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityMember;
import com.example.forum.model.community.CommunityRole;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityMemberRepository;
import com.example.forum.repository.community.CommunityRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CommunityValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityServiceImplTest {

    @InjectMocks
    private CommunityServiceImpl communityService;

    @Mock
    private AuthValidator authValidator;

    @Mock
    private CommunityValidator communityValidator;

    @Mock
    private CommunityMemberRepository communityMemberRepository;

    @Mock
    private CommunityRepository communityRepository;

    @Test
    @DisplayName("Should create community successfully")
    void createCommunity_success() {
        String username = "john";
        CommunityRequestDTO dto = new CommunityRequestDTO("TestCommunity", "A test description");
        User user = mock(User.class);

        Community community = Community.builder().name(dto.getName()).creator(user).build();
        community.setId(1L);

        when(authValidator.validateUserByUsername(username)).thenReturn(user);
        when(communityRepository.save(any(Community.class))).thenAnswer(invocation -> {
            Community c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        Long result = communityService.create(dto, username);

        assertNotNull(result);
        assertEquals(1L, result);
        verify(communityRepository).save(any(Community.class));
        verify(communityMemberRepository).save(any(CommunityMember.class));
    }

    @Test
    @DisplayName("Should fail to create community when user not found")
    void createCommunity_userNotFound_shouldThrow() {
        String username = "unknown";
        CommunityRequestDTO dto = new CommunityRequestDTO("Name", "Desc");
        when(authValidator.validateUserByUsername(username)).thenThrow(new UserNotFoundException());

        assertThrows(UserNotFoundException.class, () -> communityService.create(dto, username));
    }

    @Test
    @DisplayName("Should get community detail successfully with logged-in user")
    void getCommunityDetail_success_withUser() {
        Long communityId = 1L;
        String username = "john";

        Community community = mock(Community.class);

        User user = mock(User.class);
        Profile profile = mock(Profile.class);
        when(user.getProfile()).thenReturn(profile);
        when(profile.getNickname()).thenReturn("johnNick");
        when(user.isOnline()).thenReturn(true);

        CommunityMember member = mock(CommunityMember.class);
        when(member.getUser()).thenReturn(user);
        when(member.getRole()).thenReturn(CommunityRole.MEMBER);

        when(communityValidator.validateExistingCommunity(communityId)).thenReturn(community);
        when(authValidator.validateUserByUsername(username)).thenReturn(user);
        when(communityMemberRepository.findByCommunity(community)).thenReturn(List.of(member));
        when(communityMemberRepository.findByCommunityAndUser(community, user)).thenReturn(Optional.of(member));

        CommunityDetailDTO result = communityService.getCommunityDetail(communityId, username);

        assertNotNull(result);
        verify(communityValidator).validateExistingCommunity(communityId);
    }


    @Test
    @DisplayName("Should get community detail successfully without login")
    void getCommunityDetail_success_withoutUser() {
        Long communityId = 1L;

        Community community = mock(Community.class);
        CommunityMember member = mock(CommunityMember.class);
        User user = mock(User.class);

        when(member.getUser()).thenReturn(user);
        when(user.isOnline()).thenReturn(false);
        when(communityValidator.validateExistingCommunity(communityId)).thenReturn(community);
        when(communityMemberRepository.findByCommunity(community)).thenReturn(List.of(member));

        CommunityDetailDTO result = communityService.getCommunityDetail(communityId, null);

        assertNotNull(result);
        verify(communityValidator).validateExistingCommunity(communityId);
    }

    @Test
    @DisplayName("Should throw exception when community not found")
    void getCommunityDetail_communityNotFound_shouldThrow() {
        Long communityId = 99L;
        when(communityValidator.validateExistingCommunity(communityId)).thenThrow(new CommunityNotFoundException());

        assertThrows(CommunityNotFoundException.class, () -> communityService.getCommunityDetail(communityId, null));
    }

    @Test
    @DisplayName("Should return list of my communities")
    void getMyCommunities_success() {
        String username = "john";
        User user = mock(User.class);
        Community community = mock(Community.class);
        CommunityMember membership = mock(CommunityMember.class);

        when(membership.getCommunity()).thenReturn(community);
        when(authValidator.validateUserByUsername(username)).thenReturn(user);
        when(communityMemberRepository.findByUser(user)).thenReturn(List.of(membership));

        List<CommunityPreviewDTO> result = communityService.getMyCommunities(username);

        assertEquals(1, result.size());
        verify(authValidator).validateUserByUsername(username);
    }

    @Test
    @DisplayName("Should throw exception if user not found in getMyCommunities")
    void getMyCommunities_userNotFound_shouldThrow() {
        String username = "unknown";
        when(authValidator.validateUserByUsername(username)).thenThrow(new UserNotFoundException());

        assertThrows(UserNotFoundException.class, () -> communityService.getMyCommunities(username));
    }
}
