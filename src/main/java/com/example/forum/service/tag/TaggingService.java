package com.example.forum.service.tag;

import com.example.forum.model.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaggingService {

    // Set tag
    void setTagsForPost(Long postId, List<String> rawTags);

    // add / remove
    void addTagsToPost(Long postId, List<String> rawTags);
    void removeTagsFromPost(Long postId, List<String> rawTags);

    // retrieve
    List<String> getTagNamesForPost(Long postId);
    Page<Post> getPostsByTag(String tag, Pageable pageable);

    // suggest / top tags
    List<String> suggest(String q, int limit);
    List<String> topTags(int limit);
}
