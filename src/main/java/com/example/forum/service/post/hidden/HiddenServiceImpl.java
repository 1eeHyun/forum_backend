package com.example.forum.service.post.hidden;

import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import com.example.forum.repository.post.HiddenPostRepository;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class HiddenServiceImpl implements HiddenPostService {

    private final AuthValidator authValidator;
    private final HiddenPostRepository hiddenPostRepository;

    @Override
    public boolean isHiddenByUsername(Post post, String username) {

        if (username == null) return false;
        User user = authValidator.validateUserByUsername(username);
        return hiddenPostRepository.existsByUserAndPost(user, post);
    }

    @Override
    public Set<Long> getHiddenPostIdsByUsername(String username) {

        if (username == null) return Set.of();
        User user = authValidator.validateUserByUsername(username);
        return new HashSet<>(hiddenPostRepository.findHiddenPostIdsByUser(user));
    }
}
