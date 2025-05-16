package com.example.forum.service.post;

import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;

import java.util.List;

public interface PostService {

    // Retrieve posts - GET
    List<PostResponseDTO> getAccessiblePostsByASC(String username);
    List<PostResponseDTO> getAccessiblePostsByDESC(String username);
    PostDetailDTO getPostDetail(Long postId, String username);

    // Post posts - POST
    PostResponseDTO createPost(PostRequestDTO dto, String username);
    PostResponseDTO updatePost(Long postId, PostRequestDTO dto, String username);

    // Delete a post - DELETE
    void deletePost(Long postId, String username);

}
