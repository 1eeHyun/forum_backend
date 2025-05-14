package com.example.forum.service.profile;

import com.example.forum.dto.profile.ProfileResponseDTO;
import com.example.forum.mapper.ProfileMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityRepository;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.repository.user.UserRepository;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final AuthValidator userValidator;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final PostRepository postRepository;

    @Override
    public ProfileResponseDTO getProfile(String targetUsername, String loginUsername) {

        User user = userValidator.validateUserByUsername(targetUsername);

        boolean isMe = loginUsername != null && loginUsername.equals(targetUsername);

        List<Post> posts;

        if (isMe) {
            // See my profile
            posts = postRepository.findAllByAuthorOrderByCreatedAtDesc(user);
        } else {

            // See another person's profile
            User currentUser = loginUsername != null
                    ? userRepository.findByUsername(loginUsername).orElse(null)
                    : null;


            List<Community> sharedCommunities = currentUser != null
                    ? communityRepository.findAllByMembersContaining(currentUser)
                    : List.of();

            posts = postRepository.findVisiblePostsForViewer(user, sharedCommunities);
        }

        ProfileResponseDTO dto = ProfileMapper.toDTO(user, isMe, posts);
        return dto;
    }
}
