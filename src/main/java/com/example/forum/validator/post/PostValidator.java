package com.example.forum.validator.post;

import com.example.forum.exception.post.PostNotAuthorException;
import com.example.forum.exception.post.PostNotFoundException;
import com.example.forum.model.post.Post;
import com.example.forum.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final PostRepository postRepository;

    public Post validatePost(Long id, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);

        if (!post.getAuthor().getUsername().equals(username))
            throw new PostNotAuthorException();

        return post;
    }
}
