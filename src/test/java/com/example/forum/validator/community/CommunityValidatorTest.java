package com.example.forum.validator.community;

import com.example.forum.exception.auth.ForbiddenException;
import com.example.forum.exception.auth.UnauthorizedException;
import com.example.forum.exception.community.CommunityExistsNameException;
import com.example.forum.exception.community.CommunityNotFoundException;
import com.example.forum.exception.community.UserNotMemberException;
import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityMember;
import com.example.forum.model.community.CommunityRole;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityMemberRepository;
import com.example.forum.repository.community.CommunityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommunityValidator Tests")
class CommunityValidatorTest {

    @InjectMocks
    private CommunityValidator communityValidator;

    @Mock
    private CommunityRepository communityRepository;

    @Mock
    private CommunityMemberRepository communityMemberRepository;

    @Test
    @DisplayName("validateExistingCommunity - should return community if found")
    void validateExistingCommunity_success() {
        Community community = Community.builder().id(1L).build();
        when(communityRepository.findById(1L)).thenReturn(Optional.of(community));

        Community result = communityValidator.validateExistingCommunity(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("validateExistingCommunity - should throw if community not found")
    void validateExistingCommunity_notFound() {
        when(communityRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CommunityNotFoundException.class, () -> {
            communityValidator.validateExistingCommunity(1L);
        });
    }

    @Test
    @DisplayName("validateUniqueName - should throw if name already exists")
    void validateUniqueName_alreadyExists() {
        when(communityRepository.existsByName("dev")).thenReturn(true);

        assertThrows(CommunityExistsNameException.class, () -> {
            communityValidator.validateUniqueName("dev");
        });
    }

    @Test
    @DisplayName("validateUniqueName - should pass if name is unique")
    void validateUniqueName_unique() {
        when(communityRepository.existsByName("unique")).thenReturn(false);

        assertDoesNotThrow(() -> {
            communityValidator.validateUniqueName("unique");
        });
    }

    @Test
    @DisplayName("validateMemberCommunity - should return community if user is member")
    void validateMemberCommunity_success() {
        Community community = Community.builder().id(1L).build();
        User user = User.builder().id(10L).build();

        when(communityRepository.findById(1L)).thenReturn(Optional.of(community));
        when(communityMemberRepository.findByCommunityIdAndUserId(1L, 10L)).thenReturn(Optional.of(new CommunityMember()));

        Community result = communityValidator.validateMemberCommunity(1L, user);

        assertEquals(community, result);
    }

    @Test
    @DisplayName("validateMemberCommunity - should throw if user is not a member")
    void validateMemberCommunity_userNotMember() {
        Community community = Community.builder().id(1L).build();
        User user = User.builder().id(10L).build();

        when(communityRepository.findById(1L)).thenReturn(Optional.of(community));
        when(communityMemberRepository.findByCommunityIdAndUserId(1L, 10L)).thenReturn(Optional.empty());

        assertThrows(UserNotMemberException.class, () -> {
            communityValidator.validateMemberCommunity(1L, user);
        });
    }

    @Test
    @DisplayName("validateManagerPermission - should pass if user is manager")
    void validateManagerPermission_success() {
        Community community = Community.builder().id(1L).build();
        User user = User.builder().id(10L).build();
        CommunityMember member = CommunityMember.builder()
                .user(user)
                .community(community)
                .role(CommunityRole.MANAGER)
                .build();

        when(communityMemberRepository.findByCommunityAndUser(community, user)).thenReturn(Optional.of(member));

        assertDoesNotThrow(() -> {
            communityValidator.validateManagerPermission(user, community);
        });
    }

    @Test
    @DisplayName("validateManagerPermission - should throw UnauthorizedException if user not in community")
    void validateManagerPermission_unauthorized() {
        Community community = Community.builder().id(1L).build();
        User user = User.builder().id(10L).build();

        when(communityMemberRepository.findByCommunityAndUser(community, user)).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> {
            communityValidator.validateManagerPermission(user, community);
        });
    }

    @Test
    @DisplayName("validateManagerPermission - should throw ForbiddenException if user is not manager")
    void validateManagerPermission_forbidden() {
        Community community = Community.builder().id(1L).build();
        User user = User.builder().id(10L).build();
        CommunityMember member = CommunityMember.builder()
                .user(user)
                .community(community)
                .role(CommunityRole.MEMBER) // not a manager
                .build();

        when(communityMemberRepository.findByCommunityAndUser(community, user)).thenReturn(Optional.of(member));

        assertThrows(ForbiddenException.class, () -> {
            communityValidator.validateManagerPermission(user, community);
        });
    }
}
