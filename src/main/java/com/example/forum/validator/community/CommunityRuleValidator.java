package com.example.forum.validator.community;

import com.example.forum.exception.community.InvalidRuleForThisCommunity;
import com.example.forum.exception.community.RuleNotFoundException;
import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityRule;
import com.example.forum.repository.community.CommunityRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommunityRuleValidator {

    private final CommunityRuleRepository communityRuleRepository;

    public CommunityRule validateExistingRuleInCommunity(Long ruleId, Community community) {

        CommunityRule rule = communityRuleRepository.findById(ruleId)
                .orElseThrow(RuleNotFoundException::new);

        if (!rule.getCommunity().getId().equals(community.getId())) {
            throw new InvalidRuleForThisCommunity();
        }

        return rule;
    }
}
