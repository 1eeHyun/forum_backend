package com.example.forum.service.post;

import com.example.forum.common.SortOrder;
import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    List<PostResponseDTO> getPagedPosts(SortOrder sort, int page, int size);
    PostDetailDTO getPostDetail(Long postId, String username);

    // Post posts - POST
    PostResponseDTO createPost(PostRequestDTO dto, String username);
    PostResponseDTO updatePost(Long postId, PostRequestDTO dto, String username);

    // Delete a post - DELETE
    void deletePost(Long postId, String username);

    String uploadImage(MultipartFile file);
}
