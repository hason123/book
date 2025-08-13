package com.example.book.controller;


import com.example.book.dto.ResponseDTO.Comment.CommentResponseDTO;
import com.example.book.entity.Comment;
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

    public CommentController(CommentServiceImpl commentServiceImpl) {
        this.commentServiceImpl = commentServiceImpl;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comments")
    public ResponseEntity<List<CommentResponseDTO>> getAllComments() {
        List<CommentResponseDTO> comments = commentServiceImpl.getComments();
        if(comments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/{id}")
    public ResponseEntity<CommentResponseDTO> getCommentById(@PathVariable long id) {
        CommentResponseDTO comment = commentServiceImpl.getComment(id);
        if(comment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/create")
    public ResponseEntity<CommentResponseDTO> createComment(@RequestBody Comment comment){
        CommentResponseDTO commentCreated = commentServiceImpl.addComment(comment);
        return new ResponseEntity<>(commentCreated, HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/comment/update/{id}")
    public ResponseEntity<CommentResponseDTO> updateComemnt(@PathVariable long id, @RequestBody Comment comment){
        CommentResponseDTO commentUpdated = commentServiceImpl.updateComment(id, comment);
        if(commentUpdated == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(commentUpdated, HttpStatus.OK);
    }
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/comment/delete/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable long id){
        commentServiceImpl.deleteComment(id);
        return ResponseEntity.ok().body("Delete successful");
    }

}


