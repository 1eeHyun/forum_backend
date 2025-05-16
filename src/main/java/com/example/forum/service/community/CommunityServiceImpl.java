package com.example.forum.service.community;

import com.example.forum.dto.community.CommunityRequestDTO;
import com.example.forum.dto.community.CommunityResponseDTO;
import com.example.forum.mapper.community.CommunityMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CommunityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {


    private final AuthValidator authValidator;
    private final CommunityValidator communityValidator;
    private final CommunityRepository communityRepository;

    @Value("${app.default-community-image}")
    private String defaultCommunityImageUrl;

    @Override
    public CommunityResponseDTO create(CommunityRequestDTO dto, String username) {

        User user = authValidator.validateUserByUsername(username);

        communityValidator.validateUniqueName(dto.getName());

        Community community = Community.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .imageUrl(defaultCommunityImageUrl)
                .creator(user)
                .members(new HashSet<>())
                .build();

        community.addMember(user);
        communityRepository.save(community);

        return CommunityMapper.toDTO(community);
    }

    @Override
    public List<CommunityResponseDTO> getMyCommunities(String username) {

        User user = authValidator.validateUserByUsername(username);
        return communityRepository.findAllByMembersContaining(user).stream()
                .map(CommunityMapper::toDTO)
                .toList();
    }

    @Override
    public CommunityResponseDTO getCommunity(Long id) {

        Community community = communityValidator.validateExistingCommunity(id);

        return CommunityMapper.toDTO(community);
    }
}
