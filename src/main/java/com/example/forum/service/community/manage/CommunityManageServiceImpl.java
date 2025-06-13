package com.example.forum.service.community;

import com.example.forum.dto.community.CategoryRequestDTO;
import com.example.forum.dto.community.CategoryResponseDTO;
import com.example.forum.dto.community.CommunityRuleRequestDTO;
import com.example.forum.dto.community.CommunityRuleResponseDTO;
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
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CommunityRuleValidator;
import com.example.forum.validator.community.CommunityValidator;
import lombok.RequiredArgsConstructor;
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

    //
    // ---------------- Community Category Related ----------------
    //
    @Override
    public List<CategoryResponseDTO> getCategories(Long id) {

        Community community = communityValidator.validateExistingCommunity(id);

        return community.getCategories().stream()
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
}
