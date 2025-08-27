package com.example.book.service;

import com.example.book.dto.ResponseDTO.Post.PostListResponseDTO;
import org.springframework.transaction.annotation.Transactional;

public interface PostReactionService {
    @Transactional
    PostListResponseDTO likePost(Long postId);

    @Transactional
    PostListResponseDTO disLikePost(Long postId);
}
