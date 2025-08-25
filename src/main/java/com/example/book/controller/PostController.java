package com.example.book.controller;

import com.example.book.dto.RequestDTO.PostRequestDTO;
import com.example.book.dto.RequestDTO.Search.SearchPostRequest;
import com.example.book.dto.ResponseDTO.Comment.CommentResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.Post.PostListResponseDTO;
import com.example.book.dto.ResponseDTO.Post.PostResponseDTO;
import com.example.book.exception.UnauthorizedException;
import com.example.book.service.PostReactionService;
import com.example.book.service.impl.CommentServiceImpl;
import com.example.book.service.impl.PostServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/library")
public class PostController {
    private final PostServiceImpl postServiceImpl;
    private final CommentServiceImpl commentServiceImpl;
    private final PostReactionService postReactionService;

    public PostController(PostServiceImpl postServiceImpl, CommentServiceImpl commentServiceImpl, PostReactionService postReactionService) {
        this.postServiceImpl = postServiceImpl;
        this.commentServiceImpl = commentServiceImpl;
        this.postReactionService = postReactionService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/posts")
    public ResponseEntity<PageResponseDTO<PostListResponseDTO>> getAllPosts(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                                            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageResponseDTO<PostListResponseDTO> posts = postServiceImpl.getAllPosts(pageable);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/post/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable long id) {
        PostResponseDTO post = postServiceImpl.getPost(id);
        return ResponseEntity.ok(post);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/post/create")
    public ResponseEntity<PostResponseDTO> createPost(@RequestBody PostRequestDTO post){
        PostResponseDTO postCreate = postServiceImpl.addPost(post);
        return new ResponseEntity<>(postCreate, HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/post/update/{id}")
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable long id, @RequestBody PostRequestDTO post) throws UnauthorizedException {
        PostResponseDTO postUpdate = postServiceImpl.updatePost(id, post);
        return new ResponseEntity<>(postUpdate, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/post/delete/{id}")
    public ResponseEntity<String> deletePost(@PathVariable long id){
        postServiceImpl.deletePost(id);
        return ResponseEntity.status(200).body("Delete successful");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/post/{postId}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByPostId(@PathVariable long postId) {
        List<CommentResponseDTO> commentPosts =  commentServiceImpl.getCommentByPost(postId);
        return ResponseEntity.ok(commentPosts);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/post/search")
    public ResponseEntity<PageResponseDTO<PostListResponseDTO>> searchPost(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                                           @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize, SearchPostRequest request) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageResponseDTO<PostListResponseDTO> posts = postServiceImpl.searchPost(pageable, request);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/post/dashboard")
    public ResponseEntity<?> getPostDashboard(HttpServletResponse response) throws IOException {
        postServiceImpl.createPostWorkbook(response);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/post/like/{id}")
    public ResponseEntity<?> likePost(@PathVariable Long id){
        postReactionService.likePost(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/post/dislike/{id}")
    public ResponseEntity<?> dislikePost(@PathVariable Long id){
        postReactionService.disLikePost(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
