package com.example.book.service;

import org.springframework.transaction.annotation.Transactional;

public interface CommentReactionService {
    @Transactional
    void likeComment(Long commentId);

    @Transactional
    void dislikeComment(Long commentId);
}
