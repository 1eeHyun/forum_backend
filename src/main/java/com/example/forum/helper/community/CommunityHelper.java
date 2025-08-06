package com.example.forum.helper.community;

import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityFavoriteRepository;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommunityHelper {

    private final AuthValidator authValidator;
    private final CommunityFavoriteRepository communityFavoriteRepository;

    public Set<Long> getFavoriteCommunityIdsByUsername(String username) {

        if (username == null) return Set.of();

        User user = authValidator.validateUserByUsername(username);
        return communityFavoriteRepository.findAllByUser(user).stream()
                .map(fav -> fav.getCommunity().getId())
                .collect(Collectors.toSet());
    }
}
