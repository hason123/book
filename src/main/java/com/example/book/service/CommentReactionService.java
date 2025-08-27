package com.example.book.service;

import com.example.book.dto.ResponseDTO.Comment.CommentShortResponseDTO;
import org.springframework.transaction.annotation.Transactional;

public interface CommentReactionService {
    @Transactional
    CommentShortResponseDTO likeComment(Long commentId);

    @Transactional
    CommentShortResponseDTO dislikeComment(Long commentId);
}
