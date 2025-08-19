package com.example.book.service.impl;

import com.example.book.config.MessageConfig;
import com.example.book.constant.ReactionType;
import com.example.book.entity.Comment;
import com.example.book.entity.CommentReaction;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.repository.CommentReactionRepository;
import com.example.book.repository.CommentRepository;
import com.example.book.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentReactionServiceImpl {
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

    public void likeComment(Long commentId) {
        Long currentUserID = userServiceImpl.getCurrentUser().getUserId();
        if (commentReactionRepository.findByUser_UserIdAndComment_CommentId(currentUserID, commentId) != null) {
            Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(COMMENT_NOT_FOUND,commentId)));
            CommentReaction commentReacted = commentReactionRepository.findByUser_UserIdAndComment_CommentId(currentUserID, commentId);
            if (commentReacted.getReactionType().equals(ReactionType.LIKE)) {
                commentReactionRepository.delete(commentReacted);
                comment.setLikesCount(comment.getLikesCount() - 1);
                commentRepository.save(comment);
            }
            if (commentReacted.getReactionType().equals(ReactionType.DISLIKE)) {
                commentReacted.setReactionType(ReactionType.LIKE);
                commentReactionRepository.save(commentReacted);
                comment.setLikesCount(comment.getLikesCount() + 1);
                comment.setDislikesCount(comment.getDislikesCount() - 1);
                commentRepository.save(comment);
            }
        } else {
            CommentReaction commentReaction = new CommentReaction();
            Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(COMMENT_NOT_FOUND,commentId)));
            comment.setLikesCount(comment.getLikesCount() + 1);
            commentRepository.save(comment);
            commentReaction.setComment(comment);
            commentReaction.setReactionType(ReactionType.LIKE);
            commentReaction.setUser(userRepository.findById(currentUserID).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(USER_NOT_FOUND,currentUserID))));
            commentReactionRepository.save(commentReaction);
        }
    }

    public void dislikeComment(Long commentId) {
        Long currentUserID = userServiceImpl.getCurrentUser().getUserId();
        if (commentReactionRepository.findByUser_UserIdAndComment_CommentId(currentUserID, commentId) != null) {
            Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(COMMENT_NOT_FOUND,commentId)));
            CommentReaction commentReacted = commentReactionRepository.findByUser_UserIdAndComment_CommentId(currentUserID, commentId);
            if (commentReacted.getReactionType().equals(ReactionType.DISLIKE)) {
                commentReactionRepository.delete(commentReacted);
                comment.setDislikesCount(comment.getDislikesCount() - 1);
                commentRepository.save(comment);
            }
            if (commentReacted.getReactionType().equals(ReactionType.LIKE)) {
                commentReacted.setReactionType(ReactionType.DISLIKE);
                commentReactionRepository.save(commentReacted);
                comment.setLikesCount(comment.getLikesCount() - 1);
                comment.setDislikesCount(comment.getDislikesCount() + 1);
                commentRepository.save(comment);
            }
        } else {
            CommentReaction commentReaction = new CommentReaction();
            Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(COMMENT_NOT_FOUND,commentId)));
            comment.setLikesCount(comment.getDislikesCount() + 1);
            commentRepository.save(comment);
            commentReaction.setComment(comment);
            commentReaction.setReactionType(ReactionType.DISLIKE);
            commentReaction.setUser(userRepository.findById(currentUserID).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(USER_NOT_FOUND,currentUserID))));
            commentReactionRepository.save(commentReaction);
        }
    }


}









