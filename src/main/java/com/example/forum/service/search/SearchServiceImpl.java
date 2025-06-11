package com.example.forum.service.search;

import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.dto.profile.ProfilePreviewDTO;
import com.example.forum.dto.search.SearchResponseDTO;
import com.example.forum.mapper.community.CommunityMapper;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.mapper.profile.ProfileMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityRepository;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    // Repositories
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;

    @Override
    public SearchResponseDTO searchAll(String keyword) {

        List<PostPreviewDTO> posts = searchPosts(keyword);
        List<CommunityPreviewDTO> communities = searchCommunities(keyword);
        List<ProfilePreviewDTO> users = searchUsers(keyword);

        return SearchResponseDTO.builder()
                .users(users)
                .communities(communities)
                .posts(posts)
                .build();
    }

    @Override
    public List<ProfilePreviewDTO> searchUsers(String keyword) {

        List<User> users = userRepository.findTop5ByNicknameContainingIgnoreCase(keyword);

        return users.stream()
                .map(ProfileMapper::toProfilePreviewDTO)
                .toList();
    }

    @Override
    public List<PostPreviewDTO> searchPosts(String keyword) {

        List<Post> posts = postRepository.findTop5ByTitleContainingIgnoreCase(keyword);

        return posts.stream()
                .map(PostMapper::toPreviewDTO)
                .toList();
    }

    @Override
    public List<CommunityPreviewDTO> searchCommunities(String keyword) {

        List<Community> communities = communityRepository.findTop5ByNameContainingIgnoreCase(keyword);

        return communities.stream()
                .map(CommunityMapper::toPreviewDTO)
                .toList();
    }
}
