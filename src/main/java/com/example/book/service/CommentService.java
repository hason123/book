package com.example.book.service;

import com.example.book.dto.ResponseDTO.Comment.CommentCreateResponseDTO;
import com.example.book.dto.ResponseDTO.Comment.CommentResponseDTO;
import com.example.book.dto.ResponseDTO.Comment.CommentUpdateResponseDTO;
import com.example.book.entity.Comment;

import java.util.List;

public interface CommentService {
    CommentCreateResponseDTO addComment(Comment comment);

    List<CommentResponseDTO> getComments();

    CommentResponseDTO getComment(Long id);

    CommentUpdateResponseDTO updateComment(Long id, Comment comment);

    void deleteComment(Long id);

}
