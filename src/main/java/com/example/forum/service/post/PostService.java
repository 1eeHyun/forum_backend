package com.example.forum.service.post;

import com.example.forum.common.SortOrder;
import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    List<PostResponseDTO> getPagedPosts(SortOrder sort, int page, int size, String username);
    PostDetailDTO getPostDetail(Long postId, String username);

    List<PostPreviewDTO> getTopPostsThisWeek(String username);

    // Post posts - POST
    PostResponseDTO createPost(PostRequestDTO dto, String username);
    PostResponseDTO updatePost(Long postId, PostRequestDTO dto, String username);

    // Delete a post - DELETE
    void deletePost(Long postId, String username);

    String uploadFile(MultipartFile file);

    List<PostPreviewDTO> getRecentlyViewedPosts(String username);
    List<PostPreviewDTO> getPreviewPostsByIds(List<Long> ids, String username);

    // Toggle hide/unhide
    void toggleHidePost(Long postId, String username);

    // Get IDs of posts hidden by the user (for filtering)
    List<Long> getHiddenPostIds(String username);
}
