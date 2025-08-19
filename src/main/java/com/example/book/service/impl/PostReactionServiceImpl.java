package com.example.book.service.impl;

import com.example.book.config.MessageConfig;
import com.example.book.constant.ReactionType;
import com.example.book.entity.Post;
import com.example.book.entity.PostReaction;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.repository.PostReactionRepository;
import com.example.book.repository.PostRepository;
import com.example.book.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class PostReactionServiceImpl {
    private final PostReactionRepository postReactionRepository;
    private final UserServiceImpl userServiceImpl;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final MessageConfig messageConfig;
    private final String POST_NOT_FOUND = "error.post.notfound";
    private final String USER_NOT_FOUND = "error.user.notfound";


    public PostReactionServiceImpl(PostReactionRepository postReactionRepository, UserServiceImpl userServiceImpl, UserRepository userRepository, PostRepository postRepository, MessageConfig messageConfig) {
        this.postReactionRepository = postReactionRepository;
        this.userServiceImpl = userServiceImpl;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.messageConfig = messageConfig;
    }

    public void likePost(Long postId) {
        Long currentUserID = userServiceImpl.getCurrentUser().getUserId();
        if (postReactionRepository.findByUser_UserIdAndPost_PostId(currentUserID, postId) != null) {
            Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(POST_NOT_FOUND, postId)));
            PostReaction postReacted = postReactionRepository.findByUser_UserIdAndPost_PostId(currentUserID, postId);
            if (postReacted.getReactionType().equals(ReactionType.LIKE)) {
                postReactionRepository.delete(postReacted);
                post.setLikesCount(post.getLikesCount() - 1);
                postRepository.save(post);
            }
            if (postReacted.getReactionType().equals(ReactionType.DISLIKE)) {
               postReacted.setReactionType(ReactionType.LIKE);
               postReactionRepository.save(postReacted);
               post.setLikesCount(post.getLikesCount() + 1);
               post.setDislikesCount(post.getDislikesCount() - 1);
               postRepository.save(post);
            }
        } else {
            PostReaction postReaction = new PostReaction();
            Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(POST_NOT_FOUND, postId)));
            post.setLikesCount(post.getLikesCount() + 1);
            postRepository.save(post);
            postReaction.setPost(post);
            postReaction.setReactionType(ReactionType.LIKE);
            postReaction.setUser(userRepository.findById(currentUserID).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(USER_NOT_FOUND, currentUserID))));
            postReactionRepository.save(postReaction);
        }
    }

    public void disLikePost(Long postId) {
        Long currentUserID = userServiceImpl.getCurrentUser().getUserId();
        if (postReactionRepository.findByUser_UserIdAndPost_PostId(currentUserID, postId) != null) {
            Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(POST_NOT_FOUND, postId)));
            PostReaction postReacted = postReactionRepository.findByUser_UserIdAndPost_PostId(currentUserID, postId);
            if (postReacted.getReactionType().equals(ReactionType.DISLIKE)) {
                postReactionRepository.delete(postReacted);
                post.setLikesCount(post.getDislikesCount() - 1);
                postRepository.save(post);
            }
            if (postReacted.getReactionType().equals(ReactionType.LIKE)) {
                postReacted.setReactionType(ReactionType.DISLIKE);
                post.setLikesCount(post.getLikesCount() - 1);
                post.setDislikesCount(post.getDislikesCount() + 1);
                postReactionRepository.save(postReacted);
                postRepository.save(post);
            }
        } else {
            PostReaction postReaction = new PostReaction();
            Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(POST_NOT_FOUND, postId)));
            post.setDislikesCount(post.getDislikesCount() + 1);
            postRepository.save(post);
            postReaction.setPost(post);
            postReaction.setReactionType(ReactionType.DISLIKE);
            postReaction.setUser(userRepository.findById(currentUserID).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(USER_NOT_FOUND, currentUserID))));
            postReactionRepository.save(postReaction);
        }
    }


}







