package com.example.book.service;

import com.example.book.dto.RequestDTO.CommentRequestDTO;
import com.example.book.dto.RequestDTO.Search.SearchCommentRequest;
import com.example.book.dto.ResponseDTO.Comment.CommentResponseDTO;
import com.example.book.dto.ResponseDTO.Comment.CommentShortResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.entity.Comment;
import com.example.book.exception.UnauthorizedException;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CommentService {

    CommentShortResponseDTO addComment(Long postId, CommentRequestDTO request);

    CommentShortResponseDTO updateComment(Long postId, Long commentId, CommentRequestDTO request) throws UnauthorizedException;

    PageResponseDTO<CommentShortResponseDTO> getComments(Pageable pageable);

    CommentShortResponseDTO getComment(Long id);

    void deleteComment(Long id);

    //List<CommentResponseDTO> getCommentByPost(Long postId);

    PageResponseDTO<CommentShortResponseDTO> searchComment(Pageable pageable, SearchCommentRequest request);

    CommentResponseDTO convertCommentToDTO(Comment comment);

    CommentShortResponseDTO convertCommentToShortDTO(Comment comment);
}
