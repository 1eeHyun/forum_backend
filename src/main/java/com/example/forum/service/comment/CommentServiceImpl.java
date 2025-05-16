package com.example.forum.service.comment;

import com.example.forum.dto.comment.CommentRequestDTO;
import com.example.forum.dto.comment.CommentResponseDTO;
import com.example.forum.mapper.comment.CommentMapper;
import com.example.forum.model.comment.Comment;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import com.example.forum.repository.comment.CommentRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.comment.CommentValidator;
import com.example.forum.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentValidator commentValidator;
    private final CommentRepository commentRepository;
    private final PostValidator postValidator;
    private final AuthValidator userValidator;

    @Override
    public CommentResponseDTO createComment(String username, CommentRequestDTO dto) {

        Post post = postValidator.validatePost(dto.getPostId());
        User user = userValidator.validateUserByUsername(username);

        Comment parent = null;
        if (dto.getParentCommentId() != null)
            parent = commentValidator.validateCommentId(dto.getParentCommentId());

        Comment comment = Comment.builder()
                .post(post)
                .author(user)
                .content(dto.getContent())
                .parentComment(parent)
                .build();

        Comment saved = commentRepository.save(comment);
        return CommentMapper.toDTO(saved);
    }

    @Override
    public CommentResponseDTO createReply(String username, CommentRequestDTO dto) {

        User user = userValidator.validateUserByUsername(username);
        Comment parent = commentValidator.validateCommentId(dto.getParentCommentId());

        Comment reply = Comment.builder()
                .post(parent.getPost())
                .author(user)
                .parentComment(parent)
                .content(dto.getContent())
                .build();

        return CommentMapper.toDTO(commentRepository.save(reply));
    }

    @Override
    public List<CommentResponseDTO> getCommentsByPostId(Long postId) {

        List<Comment> topLevelComments = commentRepository.findTopLevelCommentsWithReplies(postId);
        return topLevelComments.stream()
                .map(CommentMapper::toDTO)
                .toList();
    }

    @Override
    public void deleteComment(Long commentId, String username) {

        Comment comment = commentValidator.validateCommentId(commentId);
        commentValidator.validateCommentAuthor(comment.getAuthor().getUsername(), username);

        comment.getReplies().size();
        commentRepository.delete(comment);
    }
}
