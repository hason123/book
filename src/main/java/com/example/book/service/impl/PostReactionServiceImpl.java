package com.example.book.service.impl;

import com.example.book.constant.ReactionType;
import com.example.book.entity.Post;
import com.example.book.entity.PostReaction;
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

    public PostReactionServiceImpl(PostReactionRepository postReactionRepository, UserServiceImpl userServiceImpl,  UserRepository userRepository, PostRepository postRepository) {
        this.postReactionRepository = postReactionRepository;
        this.userServiceImpl = userServiceImpl;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public void likePost(Long postId) {
        Long currentUserID = userServiceImpl.getCurrentUser().getUserId();
        if (postReactionRepository.findByUserIdAndPostId(postId, currentUserID) != null) {
            Post post = postRepository.findById(postId).get();
            PostReaction postReacted = postReactionRepository.findByUserIdAndPostId(postId, currentUserID);
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
            Post post = postRepository.findById(postId).get();
            post.setLikesCount(post.getLikesCount() + 1);
            postRepository.save(post);
            postReaction.setPost(post);
            postReaction.setReactionType(ReactionType.LIKE);
            postReaction.setUser(userRepository.findById(currentUserID).get());
            postReactionRepository.save(postReaction);
        }
    }

    public void disLikePost(Long postId) {
        Long currentUserID = userServiceImpl.getCurrentUser().getUserId();
        if (postReactionRepository.findByUserIdAndPostId(postId, currentUserID) != null) {
            Post post = postRepository.findById(postId).get();
            PostReaction postReacted = postReactionRepository.findByUserIdAndPostId(postId, currentUserID);
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
            Post post = postRepository.findById(postId).get();
            post.setDislikesCount(post.getDislikesCount() + 1);
            postRepository.save(post);
            postReaction.setPost(post);
            postReaction.setReactionType(ReactionType.DISLIKE);
            postReaction.setUser(userRepository.findById(currentUserID).get());
            postReactionRepository.save(postReaction);
        }
    }


}







