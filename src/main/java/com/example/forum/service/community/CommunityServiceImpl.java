package com.example.forum.service.community;

import com.example.forum.dto.community.CommunityRequestDTO;
import com.example.forum.dto.community.CommunityResponseDTO;
import com.example.forum.mapper.CommunityMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CommunityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {


    private final AuthValidator authValidator;
    private final CommunityValidator communityValidator;
    private final CommunityRepository communityRepository;

    @Override
    public CommunityResponseDTO create(CommunityRequestDTO dto, String username) {

        User user = authValidator.validateUser(username);

        communityValidator.validateUniqueName(dto.getName());

        Community community = Community.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .creator(user)
                .members(new HashSet<>())
                .build();

        community.addMember(user);
        communityRepository.save(community);

        return CommunityMapper.toDTO(community);
    }

    @Override
    public List<CommunityResponseDTO> getMyCommunities(String username) {

        User user = authValidator.validateUser(username);
        return communityRepository.findAllByMembersContaining(user).stream()
                .map(CommunityMapper::toDTO)
                .toList();
    }
}
