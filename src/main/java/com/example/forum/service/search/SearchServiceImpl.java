package com.example.forum.service.search;

import com.example.forum.dto.search.SearchResponseDTO;
import com.example.forum.mapper.community.CommunityMapper;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.repository.community.CommunityRepository;
import com.example.forum.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;

    @Override
    public SearchResponseDTO searchAll(String keyword) {

        List<Post> posts = postRepository.findTop5ByTitleContainingIgnoreCase(keyword);
        List<Community> communities = communityRepository.findTop5ByNameContainingIgnoreCase(keyword);

        return SearchResponseDTO.builder()
                .posts(posts.stream()
                        .map(PostMapper::toPreviewDTO)
                        .toList())
                .communities(communities.stream()
                        .map(CommunityMapper::toPreviewDTO).toList())
                .build();
    }
}
