package com.example.forum.service.community.manage;

import com.example.forum.dto.community.CategoryRequestDTO;
import com.example.forum.dto.community.CategoryResponseDTO;
import com.example.forum.dto.community.CommunityRuleRequestDTO;
import com.example.forum.dto.community.CommunityRuleResponseDTO;
import com.example.forum.dto.image.ImageUploadRequestDTO;
import com.example.forum.mapper.community.CommunityMapper;
import com.example.forum.model.community.Category;
import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityRule;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CategoryRepository;
import com.example.forum.repository.community.CommunityMemberRepository;
import com.example.forum.repository.community.CommunityRepository;
import com.example.forum.repository.community.CommunityRuleRepository;
import com.example.forum.service.auth.RedisService;
import com.example.forum.service.common.S3Service;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CommunityRuleValidator;
import com.example.forum.validator.community.CommunityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityManageServiceImpl implements CommunityManageService {

    // Validators
    private final AuthValidator authValidator;
    private final CommunityValidator communityValidator;
    private final CommunityRuleValidator communityRuleValidator;

    // Repositories
    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final CategoryRepository categoryRepository;
    private final CommunityRuleRepository communityRuleRepository;

    // Services
    private final RedisService redisService;
    private final S3Service s3Service;

    @Value("${app.default-community-image}")
    private String defaultCommunityProfileImageUrl;

    @Value("${app.default-banner-image}")
    private String defaultCommunityBannerImageUrl;

    //
    // ---------------- Community Category Related ----------------
    //
    @Override
    public List<CategoryResponseDTO> getCategories(Long id) {

        Community community = communityValidator.validateExistingCommunity(id);

        return community.getCategories().stream()
                .sorted(Comparator.comparing(category -> category.getName().toLowerCase()))
                .map(category -> new CategoryResponseDTO(
                        category.getId(),
                        category.getName(),
                        category.getDescription()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addCategory(Long communityId, CategoryRequestDTO dto, String username) {

        User user = authValidator.validateUserByUsername(username);
        Community community = communityValidator.validateExistingCommunity(communityId);

        communityValidator.validateManagerPermission(user, community);

        Category category = Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .community(community)
                .build();

        categoryRepository.save(category);
        community.getCategories().add(category);
    }

    //
    // ---------------- Community Rule Related ----------------
    //
    @Override
    public List<CommunityRuleResponseDTO> getRules(Long communityId) {

        Community community = communityValidator.validateExistingCommunity(communityId);

        Set<CommunityRule> rules = community.getRules();
        return rules.stream()
                .sorted(Comparator.comparing(CommunityRule::getCreatedAt))
                .map(CommunityMapper::toRuleResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void addRule(Long communityId, CommunityRuleRequestDTO request, String username) {

        Community community = communityValidator.validateExistingCommunity(communityId);
        User user = authValidator.validateUserByUsername(username);

        communityValidator.validateManagerPermission(user, community);

        CommunityRule newRule = CommunityRule.builder()
                .title(request.getTitle())
                .community(community)
                .content(request.getContent())
                .build();

        communityRuleRepository.save(newRule);
    }

    @Override
    @Transactional
    public void updateRule(Long communityId, Long ruleId, CommunityRuleRequestDTO request, String username) {

        Community community = communityValidator.validateExistingCommunity(communityId);
        User user = authValidator.validateUserByUsername(username);

        communityValidator.validateManagerPermission(user, community);

        CommunityRule rule = communityRuleValidator.validateExistingRuleInCommunity(ruleId, community);

        rule.setTitle(request.getTitle());
        rule.setContent(request.getContent());
        communityRuleRepository.save(rule);
    }

    @Override
    @Transactional
    public void deleteRule(Long communityId, Long ruleId, String username) {

        Community community = communityValidator.validateExistingCommunity(communityId);
        User user = authValidator.validateUserByUsername(username);

        communityValidator.validateManagerPermission(user, community);

        CommunityRule rule = communityRuleValidator.validateExistingRuleInCommunity(ruleId, community);
        communityRuleRepository.deleteById(rule.getId());
    }

    @Override
    @Transactional
    public void updateProfileImage(String username, Long communityId, ImageUploadRequestDTO dto) {

        User user = authValidator.validateUserByUsername(username);
        Community community = communityValidator.validateExistingCommunity(communityId);
        communityValidator.validateManagerPermission(user, community);

        if (community.getProfileImageUrl() != null && !community.getProfileImageUrl().equals(defaultCommunityProfileImageUrl))
            s3Service.delete(community.getProfileImageUrl());

        String newImageUrl = s3Service.upload(dto.getImage());

        community.setProfileImageUrl(newImageUrl);
        community.setProfileImagePositionX(dto.getPositionX());
        community.setProfileImagePositionY(dto.getPositionY());
        communityRepository.save(community);
    }

    @Override
    public void updateBannerImage(String username, Long communityId, ImageUploadRequestDTO dto) {

        User user = authValidator.validateUserByUsername(username);
        Community community = communityValidator.validateExistingCommunity(communityId);
        communityValidator.validateManagerPermission(user, community);

        if (community.getBannerImageUrl() != null && !community.getBannerImageUrl().equals(defaultCommunityBannerImageUrl))
            s3Service.delete(community.getBannerImageUrl());

        String newImageUrl = s3Service.upload(dto.getImage());

        community.setBannerImageUrl(newImageUrl);
        communityRepository.save(community);
    }
}
