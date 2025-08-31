package com.example.book.controller;

import com.example.book.dto.RequestDTO.CommentRequestDTO;
import com.example.book.dto.RequestDTO.Search.SearchCommentRequest;
import com.example.book.dto.ResponseDTO.Comment.CommentShortResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.exception.UnauthorizedException;
import com.example.book.service.CommentReactionService;
import com.example.book.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/library")
public class CommentController {
    private final CommentService commentService;
    private final CommentReactionService commentReactionService;

    public CommentController(CommentService commentService, CommentReactionService commentReactionService) {
        this.commentService = commentService;
        this.commentReactionService = commentReactionService;
    }

    @Operation(summary = "Lấy danh sách phân trang bình luận")
    @GetMapping("/comments")
    public ResponseEntity<PageResponseDTO<CommentShortResponseDTO>> getAllComments(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                                                   @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageResponseDTO<CommentShortResponseDTO> comments = commentService.getComments(pageable);
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "Lấy thông tin bình luận")
    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentShortResponseDTO> getCommentById(@PathVariable long id) {
        CommentShortResponseDTO comment = commentService.getComment(id);
        return ResponseEntity.ok(comment);
    }

    @Operation(summary = "Thêm mới bình luận")
    @PostMapping("/posts/{postId}/comments/")
    public ResponseEntity<CommentShortResponseDTO> createComment(@PathVariable Long postId , @RequestBody CommentRequestDTO request) {
        CommentShortResponseDTO commentCreated = commentService.addComment(postId, request);
        return ResponseEntity.ok(commentCreated);
    }

    @Operation(summary = "Cập nhật bình luận")
    @PutMapping("/posts/{postId}/comments/{id}")
    public ResponseEntity<CommentShortResponseDTO> updateComemnt(@PathVariable Long postId , @PathVariable Long id, @RequestBody CommentRequestDTO request) throws UnauthorizedException {
        CommentShortResponseDTO commentUpdated = commentService.updateComment(postId, id, request);
        return ResponseEntity.ok(commentUpdated);
    }

    @Operation(summary = "Xóa bình luận")
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable long id){
        Map<String, String> response = Map.of("message", "Delete successful");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Thích bình luận")
    @PostMapping("/comments/{id}/like")
    public ResponseEntity<?> likeComment(@PathVariable long id){
        CommentShortResponseDTO likedComment =  commentReactionService.likeComment(id);
        return ResponseEntity.ok(likedComment);
    }

    @Operation(summary = "Không thích bình luận")
    @PostMapping("/comments/{id}/dislike")
    public ResponseEntity<?> dislikeComment(@PathVariable long id){
        CommentShortResponseDTO dislikedComment = commentReactionService.dislikeComment(id);
        return ResponseEntity.ok(dislikedComment);
    }

    @Operation(summary = "Tìm kiếm bình luận (comments)")
    @GetMapping("/comments/search")
    public ResponseEntity<PageResponseDTO<CommentShortResponseDTO>> searchComments(
            @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
            SearchCommentRequest request
    ) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageResponseDTO<CommentShortResponseDTO> comments = commentService.searchComment(pageable, request);
        return ResponseEntity.ok(comments);
    }

}


