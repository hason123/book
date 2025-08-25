package com.example.book.service.impl;

import com.example.book.config.MessageConfig;
import com.example.book.constant.ReactionType;
import com.example.book.entity.Comment;
import com.example.book.entity.CommentReaction;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.repository.CommentReactionRepository;
import com.example.book.repository.CommentRepository;
import com.example.book.repository.UserRepository;
import com.example.book.service.CommentReactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
public class CommentReactionServiceImpl implements CommentReactionService {
    private final CommentReactionRepository commentReactionRepository;
    private final UserServiceImpl userServiceImpl;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final MessageConfig messageConfig;
    private final String COMMENT_NOT_FOUND= "error.comment.notfound";
    private final String USER_NOT_FOUND= "error.user.notfound";

    public CommentReactionServiceImpl(CommentReactionRepository commentReactionRepository, UserServiceImpl userServiceImpl, UserRepository userRepository, CommentRepository commentRepository, MessageConfig messageConfig) {
        this.commentReactionRepository = commentReactionRepository;
        this.userServiceImpl = userServiceImpl;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.messageConfig = messageConfig;
    }

    @Transactional
    @Override
    public void likeComment(Long commentId) {
        Long userId = userServiceImpl.getCurrentUser().getUserId();
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            log.error(messageConfig.getMessage(COMMENT_NOT_FOUND), commentId);
            return new ResourceNotFoundException(messageConfig.getMessage(COMMENT_NOT_FOUND, commentId));
        });
        CommentReaction reaction = commentReactionRepository.findByUser_UserIdAndComment_CommentId(userId, commentId);
        if (reaction != null) {
            if (reaction.getReactionType() == ReactionType.LIKE) {
                log.info("Removing like from comment {}", commentId);
                commentReactionRepository.delete(reaction);
                comment.setLikesCount(comment.getLikesCount() - 1);
            } else if (reaction.getReactionType() == ReactionType.DISLIKE) {
                log.info("Switching dislike to like on comment {}", commentId);
                reaction.setReactionType(ReactionType.LIKE);
                commentReactionRepository.save(reaction);
                comment.setLikesCount(comment.getLikesCount() + 1);
                comment.setDislikesCount(comment.getDislikesCount() - 1);
            }
        } else {
            log.info("User {} adds LIKE to comment {}", userId, commentId);
            CommentReaction newReaction = new CommentReaction();
            newReaction.setComment(comment);
            newReaction.setReactionType(ReactionType.LIKE);
            newReaction.setUser(userRepository.findById(userId).orElseThrow(() -> {
                log.error(messageConfig.getMessage(USER_NOT_FOUND), userId);
                return new ResourceNotFoundException(messageConfig.getMessage(USER_NOT_FOUND, userId));
            }));
            commentReactionRepository.save(newReaction);
            comment.setLikesCount(comment.getLikesCount() + 1);
        }
        commentRepository.save(comment);
        log.info("Updated comment {} reaction counts: {} likes, {} dislikes", commentId, comment.getLikesCount(), comment.getDislikesCount());
    }

    @Transactional
    @Override
    public void dislikeComment(Long commentId) {
        Long userId = userServiceImpl.getCurrentUser().getUserId();
        log.info("User {} attempts to dislike comment {}", userId, commentId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            log.error(messageConfig.getMessage(COMMENT_NOT_FOUND), commentId);
            return new ResourceNotFoundException(messageConfig.getMessage(COMMENT_NOT_FOUND, commentId));
        });
        CommentReaction reaction = commentReactionRepository.findByUser_UserIdAndComment_CommentId(userId, commentId);
        if (reaction != null) {
            if (reaction.getReactionType() == ReactionType.DISLIKE) {
                log.info("Removing dislike from comment {}", commentId);
                commentReactionRepository.delete(reaction);
                comment.setDislikesCount(comment.getDislikesCount() - 1);
            } else if (reaction.getReactionType() == ReactionType.LIKE) {
                log.info("Switching like to dislike on comment {}", commentId);
                reaction.setReactionType(ReactionType.DISLIKE);
                commentReactionRepository.save(reaction);
                comment.setLikesCount(comment.getLikesCount() - 1);
                comment.setDislikesCount(comment.getDislikesCount() + 1);
            }
        } else {
            log.info("User {} adds DISLIKE to comment {}", userId, commentId);
            CommentReaction newReaction = new CommentReaction();
            newReaction.setComment(comment);
            newReaction.setReactionType(ReactionType.DISLIKE);
            newReaction.setUser(userRepository.findById(userId).orElseThrow(() -> {
                log.error(messageConfig.getMessage(USER_NOT_FOUND), userId);
                return new ResourceNotFoundException(messageConfig.getMessage(USER_NOT_FOUND, userId));
            }));
            commentReactionRepository.save(newReaction);
            comment.setDislikesCount(comment.getDislikesCount() + 1);
        }
        commentRepository.save(comment);
        log.info("Updated comment {} reaction counts: {} likes, {} dislikes", commentId, comment.getLikesCount(), comment.getDislikesCount());
    }



}









