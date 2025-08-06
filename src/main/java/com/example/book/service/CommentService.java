package com.example.book.service;

import com.example.book.dto.ResponseDTO.Comment.CommentDTO;
import com.example.book.dto.ResponseDTO.Comment.CommentResponseDTO;
import com.example.book.entity.Comment;

import java.util.List;

public interface CommentService {
    CommentDTO addComment(Comment comment);

    List<CommentResponseDTO> getComments();

    CommentResponseDTO getComment(Long id);

    CommentDTO updateComment(Long id, Comment comment);

    void deleteComment(Long id);

}
