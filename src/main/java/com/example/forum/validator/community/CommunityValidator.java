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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommunityValidator {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;

    public Community validateMemberCommunity(Long id, User user) {
        Community community = communityRepository.findById(id)
                .orElseThrow(CommunityNotFoundException::new);

        boolean isMember = communityMemberRepository
                .findByCommunityIdAndUserId(community.getId(), user.getId())
                .isPresent();

        if (!isMember) {
            throw new UserNotMemberException();
        }

        return community;
    }

    public void validateUniqueName(String name) {
        if (communityRepository.existsByName(name)) {
            throw new CommunityExistsNameException();
        }
    }

    public Community validateExistingCommunity(Long id) {
        return communityRepository.findById(id)
                .orElseThrow(CommunityNotFoundException::new);
    }

    public void validateManagerPermission(User user, Community community) {

        CommunityMember member = communityMemberRepository.findByCommunityAndUser(community, user)
                .orElseThrow(UnauthorizedException::new);

        if (member.getRole() != CommunityRole.MANAGER)
            throw new ForbiddenException();
    }
}
