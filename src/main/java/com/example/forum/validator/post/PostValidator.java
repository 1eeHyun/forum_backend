package com.example.forum.validator.post;

import com.example.forum.dto.post.PostFileDTO;
import com.example.forum.exception.post.PostNotAuthorException;
import com.example.forum.exception.post.PostNotFoundException;
import com.example.forum.exception.post.TooManyPostFilesException;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import com.example.forum.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final PostRepository postRepository;

    public Post validatePost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);
    }

    public void validatePostAuthor(Post post, User user) {

        if (!post.getAuthor().equals(user))
            throw new PostNotAuthorException();
    }

    public Post validateDetailPostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
    }

    public void validatePostCount(List<PostFileDTO> fileUrls) {

        if (fileUrls != null && fileUrls.size() > 5)
            throw new TooManyPostFilesException();
    }
}
