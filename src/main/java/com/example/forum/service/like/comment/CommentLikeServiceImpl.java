package com.example.forum.service.like.comment;

import com.example.forum.model.comment.Comment;
import com.example.forum.model.like.CommentDislike;
import com.example.forum.model.like.CommentLike;
import com.example.forum.model.user.User;
import com.example.forum.repository.like.CommentDislikeRepository;
import com.example.forum.repository.like.CommentLikeRepository;
import com.example.forum.service.notification.NotificationHelper;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.forum.model.notification.Notification.NotificationType.COMMENT_LIKE;
import static com.example.forum.service.notification.NotificationMessageBuilder.buildCommentLikeNotification;

@Service
@RequiredArgsConstructor
public class CommentLikeServiceImpl implements CommentLikeService {

    // Validators
    private final CommentValidator commentValidator;
    private final AuthValidator userValidator;

    // Repositories
    private final CommentDislikeRepository dislikeRepo;
    private final CommentLikeRepository likeRepo;

    // Services
    private final NotificationHelper notificationHelper;

    @Override
    @Transactional
    public void toggleLike(Long commentId, String username) {

        if (username == null) return;

        Comment comment = commentValidator.validateCommentId(commentId);
        User user = userValidator.validateUserByUsername(username);

        Optional<CommentLike> likeExisting = likeRepo.findByCommentAndUser(comment, user);

        dislikeRepo.deleteByCommentAndUser(comment, user);

        if (likeExisting.isPresent()) {
            likeRepo.delete(likeExisting.get());
            return;
        }

        CommentLike like = new CommentLike();
        like.setComment(comment);
        like.setUser(user);
        likeRepo.save(like);

        // Like notification
        String message = buildCommentLikeNotification(user.getProfile().getNickname(), comment);

        notificationHelper.sendIfNotSelf(

            comment.getAuthor(),
            user,
            comment.getPost(),
            comment,
            COMMENT_LIKE,
            message
        );
    }

    @Override
    @Transactional
    public void toggleDislike(Long commentId, String username) {

        if (username == null) return;

        Comment comment = commentValidator.validateCommentId(commentId);
        User user = userValidator.validateUserByUsername(username);

        Optional<CommentDislike> dislikeExisting = dislikeRepo.findByCommentAndUser(comment, user);

        likeRepo.deleteByCommentAndUser(comment, user);

        if (dislikeExisting.isPresent()) {
            dislikeRepo.delete(dislikeExisting.get());
            return;
        }

        CommentDislike dislike = new CommentDislike();
        dislike.setComment(comment);
        dislike.setUser(user);
        dislikeRepo.save(dislike);
    }

    @Override
    public long countLikes(Long commentId) {
        Comment comment = commentValidator.validateCommentId(commentId);
        return likeRepo.countByComment(comment);
    }

    @Override
    public boolean hasUserLiked(Long commentId, String username) {

        if (username == null) return false;

        User user = userValidator.validateUserByUsername(username);
        Comment comment = commentValidator.validateCommentId(commentId);

        return likeRepo.existsByCommentAndUser(comment, user);
    }

    @Override
    public long countDislikes(Long commentId) {

        Comment comment = commentValidator.validateCommentId(commentId);
        return dislikeRepo.countByComment(comment);
    }

    @Override
    public boolean hasUserDisliked(Long commentId, String username) {

        if (username == null) return false;

        User user = userValidator.validateUserByUsername(username);
        Comment comment = commentValidator.validateCommentId(commentId);

        return dislikeRepo.existsByCommentAndUser(comment, user);
    }
}
