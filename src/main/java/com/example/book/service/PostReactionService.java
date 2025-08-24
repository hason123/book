package com.example.book.service;

import org.springframework.transaction.annotation.Transactional;

public interface PostReactionService {
    @Transactional
    void likePost(Long postId);

    @Transactional
    void disLikePost(Long postId);
}
