package com.example.book.service.impl;

import com.example.book.config.MessageConfig;
import com.example.book.constant.MessageError;
import com.example.book.constant.ReactionType;
import com.example.book.dto.ResponseDTO.Post.PostListResponseDTO;
import com.example.book.entity.Post;
import com.example.book.entity.PostReaction;
import com.example.book.entity.User;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.repository.PostReactionRepository;
import com.example.book.repository.PostRepository;
import com.example.book.repository.UserRepository;
import com.example.book.service.PostReactionService;
import com.example.book.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
public class PostReactionServiceImpl implements PostReactionService {
    private final PostReactionRepository postReactionRepository;
    private final UserServiceImpl userServiceImpl;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final MessageConfig messageConfig;
    private final PostService postService;

    public PostReactionServiceImpl(PostReactionRepository postReactionRepository, UserServiceImpl userServiceImpl, UserRepository userRepository, PostRepository postRepository, MessageConfig messageConfig, PostService postService) {
        this.postReactionRepository = postReactionRepository;
        this.userServiceImpl = userServiceImpl;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.messageConfig = messageConfig;
        this.postService = postService;
    }

    @Transactional
    @Override
    public PostListResponseDTO likePost(Long postId) {
        Long userId = userServiceImpl.getCurrentUser().getUserId();
        log.info("User {} attempts to like post {}", userId, postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post {} not found for like action", postId);
                    return new ResourceNotFoundException(messageConfig.getMessage(MessageError.POST_NOT_FOUND, postId));
                });
        PostReaction reaction = postReactionRepository.findByUser_UserIdAndPost_PostId(userId, postId);
        if (reaction != null) {
            if (reaction.getReactionType() == ReactionType.LIKE) {
                log.info("User {} removes like from post {}", userId, postId);
                postReactionRepository.delete(reaction);
                post.setLikesCount(post.getLikesCount() - 1);
            } else if (reaction.getReactionType() == ReactionType.DISLIKE) {
                log.info("User {} switches reaction from DISLIKE to LIKE on post {}", userId, postId);
                reaction.setReactionType(ReactionType.LIKE);
                postReactionRepository.save(reaction);
                post.setLikesCount(post.getLikesCount() + 1);
                post.setDislikesCount(post.getDislikesCount() - 1);
            }
        } else {
            log.info("User {} adds LIKE to post {}", userId, postId);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("User {} not found when reacting to post {}", userId, postId);
                        return new ResourceNotFoundException(messageConfig.getMessage(MessageError.USER_NOT_FOUND, userId));
                    });
            reaction = new PostReaction();
            reaction.setPost(post);
            reaction.setUser(user);
            reaction.setReactionType(ReactionType.LIKE);
            postReactionRepository.save(reaction);
            post.setLikesCount(post.getLikesCount() + 1);
        }
        postRepository.save(post);
        log.info("Updated post {} reaction counts: {} likes, {} dislikes", postId, post.getLikesCount(), post.getDislikesCount());
        return postService.convertPostListToDTO(post);
    }

    @Transactional
    @Override
    public PostListResponseDTO disLikePost(Long postId) {
        Long userId = userServiceImpl.getCurrentUser().getUserId();
        log.info("User {} attempts to dislike post {}", userId, postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post {} not found for dislike action", postId);
                    return new ResourceNotFoundException(messageConfig.getMessage(MessageError.POST_NOT_FOUND, postId));
                });
        PostReaction reaction = postReactionRepository.findByUser_UserIdAndPost_PostId(userId, postId);
        if (reaction != null) {
            if (reaction.getReactionType() == ReactionType.DISLIKE) {
                log.info("User {} removes DISLIKE from post {}", userId, postId);
                postReactionRepository.delete(reaction);
                post.setDislikesCount(post.getDislikesCount() - 1);
            } else if (reaction.getReactionType() == ReactionType.LIKE) {
                log.info("User {} switches reaction from LIKE to DISLIKE on post {}", userId, postId);
                reaction.setReactionType(ReactionType.DISLIKE);
                postReactionRepository.save(reaction);
                post.setLikesCount(post.getLikesCount() - 1);
                post.setDislikesCount(post.getDislikesCount() + 1);
            }
        } else {
            log.info("User {} adds DISLIKE to post {}", userId, postId);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("User {} not found when reacting to post {}", userId, postId);
                        return new ResourceNotFoundException(messageConfig.getMessage(MessageError.USER_NOT_FOUND, userId));
                    });
            reaction = new PostReaction();
            reaction.setPost(post);
            reaction.setUser(user);
            reaction.setReactionType(ReactionType.DISLIKE);
            postReactionRepository.save(reaction);
            post.setDislikesCount(post.getDislikesCount() + 1);
        }
        postRepository.save(post);
        log.info("Updated post {} reaction counts: {} likes, {} dislikes", postId, post.getLikesCount(), post.getDislikesCount());
        return postService.convertPostListToDTO(post);
    }




}







