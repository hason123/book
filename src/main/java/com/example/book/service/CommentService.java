package com.example.book.service;

import com.example.book.dto.ResponseDTO.Comment.CommentResponseDTO;
import com.example.book.entity.Comment;

import java.util.List;

public interface CommentService {
    CommentResponseDTO addComment(Comment comment);

    List<CommentResponseDTO> getComments();

    CommentResponseDTO getComment(Long id);

    CommentResponseDTO updateComment(Long id, Comment comment);

    void deleteComment(Long id);

    List<CommentResponseDTO> getCommentByPost(Long postId);

}
