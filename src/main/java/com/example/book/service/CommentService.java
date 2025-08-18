package com.example.book.service;

import com.example.book.dto.ResponseDTO.Comment.CommentResponseDTO;
import com.example.book.dto.ResponseDTO.Comment.CommentShortResponseDTO;
import com.example.book.entity.Comment;
import java.util.List;

public interface CommentService {
    CommentShortResponseDTO addComment(Comment comment);

    List<CommentShortResponseDTO> getComments();

    CommentShortResponseDTO getComment(Long id);

    CommentShortResponseDTO updateComment(Long id, Comment comment);

    void deleteComment(Long id);

    List<CommentResponseDTO> getCommentByPost(Long postId);

}
