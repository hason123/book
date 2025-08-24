package com.example.book.controller;

import com.example.book.dto.RequestDTO.CommentRequestDTO;
import com.example.book.dto.ResponseDTO.Comment.CommentShortResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.entity.Comment;
import com.example.book.service.impl.CommentReactionServiceImpl;
import com.example.book.service.impl.CommentServiceImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/library")
public class CommentController {
    private final CommentServiceImpl commentServiceImpl;
    private final CommentReactionServiceImpl commentReactionServiceImpl;

    public CommentController(CommentServiceImpl commentServiceImpl, CommentReactionServiceImpl commentReactionServiceImpl) {
        this.commentServiceImpl = commentServiceImpl;
        this.commentReactionServiceImpl = commentReactionServiceImpl;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comments")
    public ResponseEntity<PageResponseDTO<CommentShortResponseDTO>> getAllComments(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                                                   @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageResponseDTO<CommentShortResponseDTO> comments = commentServiceImpl.getComments(pageable);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/{id}")
    public ResponseEntity<CommentShortResponseDTO> getCommentById(@PathVariable long id) {
        CommentShortResponseDTO comment = commentServiceImpl.getComment(id);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/post/{postId}/comment/create")
    public ResponseEntity<CommentShortResponseDTO> createComment(@PathVariable Long postId , @RequestBody CommentRequestDTO request) {
        CommentShortResponseDTO commentCreated = commentServiceImpl.addComment(postId, request);
        return new ResponseEntity<>(commentCreated, HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/post/{postId}/comment/update/{commentId}")
    public ResponseEntity<CommentShortResponseDTO> updateComemnt(@PathVariable Long postId , @PathVariable Long commentId, @RequestBody CommentRequestDTO request){
        CommentShortResponseDTO commentUpdated = commentServiceImpl.updateComment(postId, commentId, request);
        return new ResponseEntity<>(commentUpdated, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/comment/delete/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable long id){
        commentServiceImpl.deleteComment(id);
        return ResponseEntity.ok().body("Delete successful");
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/like/{id}")
    public ResponseEntity<?> likeComment(@PathVariable long id){
        commentReactionServiceImpl.likeComment(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/dislike/{id}")
    public ResponseEntity<?> dislikeComment(@PathVariable long id){
        commentReactionServiceImpl.dislikeComment(id);
        return ResponseEntity.ok().build();
    }

}


