package com.example.book.controller;

import com.example.book.dto.ResponseDTO.Comment.CommentShortResponseDTO;
import com.example.book.entity.Comment;
import com.example.book.service.impl.CommentReactionServiceImpl;
import com.example.book.service.impl.CommentServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
    public ResponseEntity<List<CommentShortResponseDTO>> getAllComments() {
        List<CommentShortResponseDTO> comments = commentServiceImpl.getComments();
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/{id}")
    public ResponseEntity<CommentShortResponseDTO> getCommentById(@PathVariable long id) {
        CommentShortResponseDTO comment = commentServiceImpl.getComment(id);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/create")
    public ResponseEntity<CommentShortResponseDTO> createComment(@RequestBody Comment comment){
        CommentShortResponseDTO commentCreated = commentServiceImpl.addComment(comment);
        return new ResponseEntity<>(commentCreated, HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/comment/update/{id}")
    public ResponseEntity<CommentShortResponseDTO> updateComemnt(@PathVariable long id, @RequestBody Comment comment){
        CommentShortResponseDTO commentUpdated = commentServiceImpl.updateComment(id, comment);
        return new ResponseEntity<>(commentUpdated, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/comment/delete/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable long id){
        commentServiceImpl.deleteComment(id);
        return ResponseEntity.ok().body("Delete successful");
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/like/{id}")
    public ResponseEntity<Void> likeComment(@PathVariable long id){
        commentReactionServiceImpl.likeComment(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/dislike/{id}")
    public ResponseEntity<Void> dislikeComment(@PathVariable long id){
        commentReactionServiceImpl.dislikeComment(id);
        return ResponseEntity.ok().build();
    }

}


