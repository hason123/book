package com.example.book.controller;


import com.example.book.dto.ResponseDTO.Comment.CommentResponseDTO;
import com.example.book.dto.ResponseDTO.Comment.CommentDTO;


import com.example.book.entity.Comment;
import com.example.book.service.impl.CommentServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/library")
public class CommentController {
    private final CommentServiceImpl commentServiceImpl;

    public CommentController(CommentServiceImpl commentServiceImpl) {
        this.commentServiceImpl = commentServiceImpl;
    }

    @GetMapping("/comments")
    public ResponseEntity<List<CommentResponseDTO>> getAllComments() {
        List<CommentResponseDTO> comments = commentServiceImpl.getComments();
        if(comments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/comment/{id}")
    public ResponseEntity<CommentResponseDTO> getPostById(@PathVariable long id) {
        CommentResponseDTO comment = commentServiceImpl.getComment(id);
        if(comment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @PostMapping("/comment/create")
    public ResponseEntity<CommentDTO> createPost(@RequestBody Comment comment){
        CommentDTO commentCreated = commentServiceImpl.addComment(comment);
        return new ResponseEntity<>(commentCreated, HttpStatus.CREATED);
    }

    @PutMapping("/comment/update/{id}")
    public ResponseEntity<CommentDTO> updatePost(@PathVariable long id, @RequestBody Comment comment){
        CommentDTO commentUpdated = commentServiceImpl.updateComment(id, comment);
        if(commentUpdated == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(commentUpdated, HttpStatus.OK);
    }

    @DeleteMapping("/comment/delete/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable long id){
        commentServiceImpl.deleteComment(id);
        return ResponseEntity.status(200).body("Delete successful");

    }
}


